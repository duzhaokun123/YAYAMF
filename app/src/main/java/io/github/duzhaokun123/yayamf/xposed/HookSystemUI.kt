package io.github.duzhaokun123.yayamf.xposed

import android.os.Build
import com.android.wm.shell.windowdecor.TaskOperations
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yaxh.init.YAXHContext
import io.github.duzhaokun123.yaxh.init.YAXHInit
import io.github.duzhaokun123.yaxh.utils.findMethod
import io.github.duzhaokun123.yaxh.utils.hookAfter
import io.github.duzhaokun123.yaxh.utils.hookAllConstructorBefore
import io.github.duzhaokun123.yaxh.utils.loadClass
import io.github.duzhaokun123.yaxh.utils.logger.ALog
import io.github.duzhaokun123.yaxh.utils.logger.XLog
import io.github.duzhaokun123.yaxh.utils.setObject
import io.github.duzhaokun123.yayamf.utils.MultiClassLoader
import io.github.duzhaokun123.yayamf.wm.shell.windowdeco.YAYAMFWindowDecorViewModel
import java.io.File

object HookSystemUI : IXposedHookLoadPackage {
    const val TAG = "YAYAMF_HookSystemUI"
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        YAXHInit.handleLoadPackage(lpparam)
        YAXHInit.setLogTag(TAG)

        val systemUiLastStartFile = File("/data/user_de/0/com.android.systemui/systemui_last_start")
        val systemUiLastStart = runCatching {
            systemUiLastStartFile.readText().toLongOrNull()
//        }.onFailure {
//            ALog.e("read systemui_last_start failed:", it)
        }.getOrNull() ?: 0
        val systemUiThisStart = System.currentTimeMillis()
        XLog.v("SystemUI last start: $systemUiLastStart, this start: $systemUiThisStart")
        runCatching {
            systemUiLastStartFile.parentFile?.mkdirs()
            systemUiLastStartFile.writeText(systemUiThisStart.toString())
        }.onFailure {
            XLog.v("it should first time start SystemUI")
        }
        val ignoreCrashCloseFile = File("/data/user_de/0/com.android.systemui/ignore_crash_close")
        if (ignoreCrashCloseFile.exists()) {
            XLog.i("ignore_crash_close exists, keep hooking SystemUI")
        } else if (systemUiThisStart - systemUiLastStart < 5000) {
            XLog.e("SystemUI restart too close, may YAYAMF caused SystemUI restart loop, abort hooking")
            return
        }

        XLog.i("hooking SystemUI")
        MultiClassLoader.addClassLoader(YAXHContext.javaClass.classLoader.parent)
        MultiClassLoader.addClassLoader(lpparam.classLoader)
        YAXHContext.javaClass.classLoader.setObject("parent", MultiClassLoader)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            loadClass("com.android.wm.shell.dagger.WMShellModule_ProvideWindowDecorViewModelFactory")
                .findMethod { name == "get" || name == "provideWindowDecorViewModel" }
                .hookAfter {
                    it.result = YAYAMFWindowDecorViewModel(it.result).toProxy()
                    ALog.d("provideWindowDecorViewModel called")
                }

        } else {
            loadClass("com.android.wm.shell.freeform.FreeformTaskListener")
                .hookAllConstructorBefore {
                    it.args[3] = YAYAMFWindowDecorViewModel(it.args[3]).toProxy()
                    ALog.d("FreeformTaskListener constructor called")
                }
        }
    }
}