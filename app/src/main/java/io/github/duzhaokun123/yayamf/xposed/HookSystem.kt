package io.github.duzhaokun123.yayamf.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.duzhaokun123.yaxh.init.YAXHContext
import io.github.duzhaokun123.yaxh.init.YAXHInit
import io.github.duzhaokun123.yaxh.utils.findMethod
import io.github.duzhaokun123.yaxh.utils.hookAfter
import io.github.duzhaokun123.yaxh.utils.loadClass
import io.github.duzhaokun123.yaxh.utils.setObject
import io.github.duzhaokun123.yayamf.utils.MultiClassLoader

object HookSystem: IXposedHookLoadPackage {
    const val TAG = "YAYAMF_HookSystem"

    val hookServices = arrayOf(
        WMHook,
    )

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        YAXHInit.handleLoadPackage(lpparam)
        YAXHInit.setLogTag(TAG)
        MultiClassLoader.addClassLoader(YAXHContext.javaClass.classLoader.parent)
        MultiClassLoader.addClassLoader(lpparam.classLoader)
        YAXHContext.javaClass.classLoader.setObject("parent", MultiClassLoader)

        loadClass("android.os.ServiceManagerProxy")
            .findMethod { name == "addService" }
            .hookAfter {
                val serviceName = it.args[0] as String
                val serviceClassLoader = it.args[1].javaClass.classLoader
                hookServices.forEach { hookService ->
                    MultiClassLoader.addClassLoader(serviceClassLoader!!)
                    if (serviceName == hookService.serviceName) {
                        hookService.onServiceAdded(serviceClassLoader)
                    }
                }
            }
    }
}