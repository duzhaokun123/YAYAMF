package io.github.duzhaokun123.yayamf.xposed

import android.app.TaskInfo
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.getObjectAs
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.loadClass
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yayamf.BuildConfig

class HookSystem: IXposedHookLoadPackage {
    companion object {
        const val TAG = "YAYAMF"
    }
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return

        EzXHelperInit.initHandleLoadPackage(lpparam)
        EzXHelperInit.setLogTag(HookSystemUI.TAG)
        Log.ix("YAYAMF ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}) HookSystem loaded")

        loadClass("com.android.server.wm.WindowManagerService")
            .findMethod { name == "addWindow" }
            .hookBefore {
                android.util.Log.d(TAG, "addWindow: ${it.args[2]}")
            }
    }
}