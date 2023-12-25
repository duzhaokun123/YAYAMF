package io.github.duzhaokun123.yayamf.wm

import android.app.WindowConfiguration
import android.graphics.Rect
import com.android.server.wm.Task
import de.robv.android.xposed.XC_MethodHook
import io.github.duzhaokun123.yaxh.utils.getObjectAs
import io.github.duzhaokun123.yaxh.utils.invokeMethodAuto
import io.github.duzhaokun123.yaxh.utils.invokeMethodAutoAs
import io.github.duzhaokun123.yaxh.utils.logger.ALog
import io.github.duzhaokun123.yayamf.utils.WINDOWING_MODE_YAYAMF
import io.github.duzhaokun123.yayamf.xposed.BaseClassProxyHook
import io.github.duzhaokun123.yaxh.utils.invokeas.invoke

object TaskDisplayArea : BaseClassProxyHook() {
    override val className: String
        get() = "com.android.server.wm.TaskDisplayArea"

    init {
        register(TaskDisplayArea::after_getOrCreateRootTask, HookType.HOOK_AFTER) {
            name == "getOrCreateRootTask" && parameterTypes.size == 7
        }
    }

    fun after_getOrCreateRootTask(param: XC_MethodHook.MethodHookParam) {
        val thiz = param.thisObject
        val build = param.result as Task
        val windowingMode = param.args[0] as Int
        val displayContent = thiz.getObjectAs</* DisplayContent */Any>("mDisplayContent")

        if (windowingMode == WINDOWING_MODE_YAYAMF) {
            ALog.d("TaskDisplayArea: after_getOrCreateRootTask: got yayamf task")
            setWindowingModeYayamf(build, WINDOWING_MODE_YAYAMF)
            ALog.d("TaskDisplayArea: after_getOrCreateRootTask: yayamf task set")
        }
    }

    fun setWindowingModeYayamf(task: Task, windowingMode: Int) {
        if (task<Any>(".getWindowConfiguration(")<Int>(".getWindowingMode(") != WINDOWING_MODE_YAYAMF) {
             task.setWindowingMode(windowingMode)
        }
        val rootTask = task.rootTask
        if (rootTask != null) {
            val rect = Rect()
            rootTask.getBounds(rect)
            rect.right = rect.left + 500
            rect.bottom = rect.top + 1000
            rootTask.setBounds(rect)
            rootTask.setAlwaysOnTop(true)
            rootTask.setWindowingMode(WINDOWING_MODE_YAYAMF)
        }
    }
}