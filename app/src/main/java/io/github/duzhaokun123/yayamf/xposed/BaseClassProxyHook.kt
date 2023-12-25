package io.github.duzhaokun123.yayamf.xposed

import de.robv.android.xposed.XC_MethodHook
import io.github.duzhaokun123.yaxh.utils.MethodFilter
import io.github.duzhaokun123.yaxh.utils.hookAfter
import io.github.duzhaokun123.yaxh.utils.hookBefore
import io.github.duzhaokun123.yaxh.utils.loadClass

typealias HookFunc = (XC_MethodHook.MethodHookParam) -> Unit

abstract class BaseClassProxyHook {
    enum class HookType {
        HOOK_BEFORE,
        HOOK_AFTER
    }


    val hooks = mutableMapOf<HookFunc, Pair<HookType, MethodFilter>>()

    abstract val className: String
    fun init(classLoader: ClassLoader) {
        hooks.forEach { (func, u) ->
            val (type, filter) = u
            loadClass(className, classLoader)
                .declaredMethods.forEach {
                    if (filter(it)) {
                        when (type) {
                            HookType.HOOK_BEFORE -> it.hookBefore { func(it) }
                            HookType.HOOK_AFTER -> it.hookAfter { func(it) }
                        }
                    }
                }
        }
    }

    fun register(func: HookFunc, type: HookType, filter: MethodFilter) {
        hooks[func] = type to filter
    }
}