package io.github.duzhaokun123.yayamf.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yaxh.init.YAXHInit
import io.github.duzhaokun123.yayamf.BuildConfig

class XposedInit: IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when(lpparam.packageName) {
            "android" -> HookSystem.handleLoadPackage(lpparam)
            "com.android.systemui" -> HookSystemUI.handleLoadPackage(lpparam)
            BuildConfig.APPLICATION_ID -> HookSelf.handleLoadPackage(lpparam)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        YAXHInit.initZygote(startupParam)
    }
}