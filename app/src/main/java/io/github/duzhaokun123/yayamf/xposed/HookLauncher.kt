package io.github.duzhaokun123.yayamf.xposed

import android.annotation.SuppressLint
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookAllConstructorBefore
import com.github.kyuubiran.ezxhelper.utils.loadClass
import com.github.kyuubiran.ezxhelper.utils.loadClassOrNull
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yayamf.BuildConfig

class HookLauncher: IXposedHookLoadPackage {
    companion object {
        const val TAG = "YAYAMF"
    }
    @SuppressLint("PrivateApi")
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        runCatching {
            lpparam.classLoader.loadClass("com.android.launcher3.Launcher")
        }.getOrNull() ?: return

        EzXHelperInit.initHandleLoadPackage(lpparam)
        EzXHelperInit.setLogTag(TAG)
        Log.ix("YAYAMF ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}) HookLauncher loaded for ${lpparam.packageName}")
    }
}