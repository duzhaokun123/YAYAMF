package io.github.duzhaokun123.yayamf.xposed

import android.annotation.SuppressLint
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.hookAllConstructorBefore
import com.github.kyuubiran.ezxhelper.utils.loadClass
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yayamf.BuildConfig
import io.github.duzhaokun123.yayamf.windowdeco.YAYAMFWindowDecorViewModel
import java.io.File

class HookSystemUI : IXposedHookLoadPackage, IXposedHookZygoteInit {
    companion object {
        const val TAG = "YAYAMF"
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelperInit.initZygote(startupParam)
    }

    @SuppressLint("SdCardPath")
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        EzXHelperInit.initHandleLoadPackage(lpparam)
        EzXHelperInit.setLogTag(TAG)
        Log.ix("YAYAMF ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}) HookSystemUI loaded")

        val systemUiLastStartFile = File("/data/data/com.android.systemui/systemui_last_start")
        val systemUiLastStart = runCatching {
            systemUiLastStartFile.readText().toLongOrNull()
        }.getOrNull() ?: 0
        val systemUiThisStart = System.currentTimeMillis()
        Log.ix("SystemUI last start: $systemUiLastStart, this start: $systemUiThisStart")
        runCatching {
            systemUiLastStartFile.writeText(systemUiThisStart.toString())
        }.onFailure {
            Log.ix("it should first time start SystemUI")
        }
        if (systemUiThisStart - systemUiLastStart < 1000) {
            Log.wx("SystemUI restart too close, may YAYAMF caused SystemUI restart loop, abort hooking")
            return
        }

        loadClass("com.android.wm.shell.freeform.FreeformTaskListener")
            .hookAllConstructorBefore {
                val originalWindowDecorViewModel = it.args[3]
                Log.ix("originalWindowDecorViewModel class name: ${originalWindowDecorViewModel::class.java.name}")
                it.args[3] = YAYAMFWindowDecorViewModel(originalWindowDecorViewModel).proxy
                Log.ix("replaced WindowDecorViewModel")
            }
    }
}