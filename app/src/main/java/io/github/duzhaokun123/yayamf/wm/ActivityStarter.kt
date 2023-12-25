package io.github.duzhaokun123.yayamf.wm

import android.app.ActivityOptions
import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import io.github.duzhaokun123.yaxh.utils.getObjectAs
import io.github.duzhaokun123.yaxh.utils.logger.ALog
import io.github.duzhaokun123.yayamf.utils.WINDOWING_MODE_YAYAMF
import io.github.duzhaokun123.yayamf.xposed.BaseClassProxyHook

object ActivityStarter : BaseClassProxyHook() {
    override val className: String
        get() = "com.android.server.wm.ActivityStarter"

    init {
        register(ActivityStarter::before_execute, HookType.HOOK_BEFORE) {
            name == "execute"
        }
    }

    fun before_execute(param: XC_MethodHook.MethodHookParam) {
        val thiz = param.thisObject
        val mRequest = thiz.getObjectAs</* ActivityStarter.Request */Any>("mRequest")
        val intent = mRequest.getObjectAs<Intent>("intent")
        val activityOptions =
            mRequest.getObjectAs</*SafeActivityOptions */Any?>("activityOptions")
        val originalOptions = activityOptions?.getObjectAs<ActivityOptions>("mOriginalOptions")
        val launchWindowingMode = originalOptions?.launchWindowingMode
        ALog.d("ActivityStarter: execute: $launchWindowingMode $intent")
        if (launchWindowingMode != WINDOWING_MODE_YAYAMF) return
//        originalOptions.launchWindowingMode = /* WindowConfiguration.WINDOWING_MODE_FREEFORM */ 5
//        ALog.d("WSM: execute: replaced")
    }
}