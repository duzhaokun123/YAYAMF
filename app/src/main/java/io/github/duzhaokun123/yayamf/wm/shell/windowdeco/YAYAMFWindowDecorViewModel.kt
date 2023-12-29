package io.github.duzhaokun123.yayamf.wm.shell.windowdeco

import android.app.ActivityManager
import android.app.IActivityTaskManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ServiceManager
import android.util.SparseArray
import android.view.SurfaceControl
import android.window.TaskOrganizer
import com.android.wm.shell.common.DisplayController
import com.android.wm.shell.common.SyncTransactionQueue
import com.android.wm.shell.splitscreen.SplitScreenController
import com.android.wm.shell.windowdecor.TaskOperations
import com.android.wm.shell.windowdecor.WindowDecorViewModel
import io.github.duzhaokun123.yaxh.utils.findConstructor
import io.github.duzhaokun123.yaxh.utils.getObjectAs
import io.github.duzhaokun123.yaxh.utils.invokeMethodAutoAs
import io.github.duzhaokun123.yaxh.utils.logger.ALog
import java.lang.reflect.Proxy

class YAYAMFWindowDecorViewModel(private val original: Any) {
    fun toProxy(): Any {
        return Proxy.newProxyInstance(original.javaClass.classLoader,
            arrayOf(WindowDecorViewModel::class.java)) { _, method, args ->
            return@newProxyInstance when (method.name) {
                "setFreeformTaskTransitionStarter" -> {
                    setFreeformTaskTransitionStarter(
                        args[0]
                    )
                }
                "setSplitScreenController" -> {
                    setSplitScreenController(
                        args[0] as SplitScreenController
                    )
                }
                "onTaskOpening" -> {
                    onTaskOpening(
                        args[0] as ActivityManager.RunningTaskInfo,
                        args[1] as SurfaceControl,
                        args[2] as SurfaceControl.Transaction,
                        args[3] as SurfaceControl.Transaction
                    )
                }
                "onTaskInfoChanged" -> {
                    onTaskInfoChanged(
                        args[0] as ActivityManager.RunningTaskInfo
                    )
                }
                "onTaskChanging" -> {
                    onTaskChanging(
                        args[0] as ActivityManager.RunningTaskInfo,
                        args[1] as SurfaceControl,
                        args[2] as SurfaceControl.Transaction,
                        args[3] as SurfaceControl.Transaction
                    )
                }
                "onTaskClosing" -> {
                    onTaskClosing(
                        args[0] as ActivityManager.RunningTaskInfo,
                        args[1] as SurfaceControl.Transaction,
                        args[2] as SurfaceControl.Transaction
                    )
                }
                "destroyWindowDecoration" -> {
                    destroyWindowDecoration(
                        args[0] as ActivityManager.RunningTaskInfo
                    )
                }
                else -> {
                    ALog.e("unknown method: $method")
                    null
                }
            }
        }
    }

    val context = original.getObjectAs<Context>("mContext")
    val syncQueue = original.getObjectAs<SyncTransactionQueue>("mSyncQueue")
    val displayController = original.getObjectAs<DisplayController>("mDisplayController")
    val taskOrganizer = original.getObjectAs<TaskOrganizer>("mTaskOrganizer")

    val windowDecorationById = SparseArray<YAYAMFWindowDecoration>()
    var taskOperations = TaskOperations::class.java
        .findConstructor { parameterCount == 3 }
        .newInstance(null, context, syncQueue) as TaskOperations
    val atm: IActivityTaskManager = IActivityTaskManager.Stub.asInterface(
        ServiceManager.getService("activity_task")
    )
    lateinit var handler: Handler

    init {
        atm.registerTaskStackListener(TaskStackListener(this))
    }

    fun setFreeformTaskTransitionStarter(transitionStarter: Any /* FreeformTaskTransitionStarter or FreeformTaskTransitionHandler */) {
        taskOperations = TaskOperations::class.java
            .findConstructor { parameterCount == 3 }
            .newInstance(transitionStarter, context, syncQueue) as TaskOperations
    }

    fun setSplitScreenController(splitScreenController: SplitScreenController) {

    }

    fun onTaskOpening(
        taskInfo: ActivityManager.RunningTaskInfo,
        taskSurface: SurfaceControl,
        startT: SurfaceControl.Transaction,
        finishT: SurfaceControl.Transaction
    ): Boolean {
        ALog.d("onTaskOpening: $taskInfo")
        if (shouldShowWindowDecor(taskInfo).not()) return false
        createWindowDecoration(taskInfo, taskSurface, startT, finishT)
        return true
    }

    fun onTaskInfoChanged(taskInfo: ActivityManager.RunningTaskInfo) {
        ALog.d("onTaskInfoChanged: $taskInfo")
        val windowDecoration = windowDecorationById[taskInfo.taskId] ?: return
        windowDecoration.relayout(taskInfo)
    }

    fun onTaskChanging(
        taskInfo: ActivityManager.RunningTaskInfo,
        taskSurface: SurfaceControl,
        startT: SurfaceControl.Transaction,
        finishT: SurfaceControl.Transaction
    ) {
        ALog.d("onTaskChanging: $taskInfo")
        val windowDecoration = windowDecorationById[taskInfo.taskId]
        if (shouldShowWindowDecor(taskInfo).not()) {
            destroyWindowDecoration(taskInfo)
            return
        }
        if (windowDecoration == null) {
            createWindowDecoration(taskInfo, taskSurface, startT, finishT)
        } else {
            windowDecoration.relayout(taskInfo, startT, finishT, false)
        }
    }

    fun onTaskClosing(
        taskInfo: ActivityManager.RunningTaskInfo,
        startT: SurfaceControl.Transaction,
        finishT: SurfaceControl.Transaction
    ) {
        ALog.d("onTaskClosing: $taskInfo")
        windowDecorationById[taskInfo.taskId]?.relayout(taskInfo, startT, finishT, false)
    }

    fun destroyWindowDecoration(taskInfo: ActivityManager.RunningTaskInfo) {
        ALog.d("destroyWindowDecoration: $taskInfo")
        windowDecorationById.removeReturnOld(taskInfo.taskId)?.close()
    }

    private fun shouldShowWindowDecor(taskInfo: ActivityManager.RunningTaskInfo): Boolean {
        return taskInfo.windowingMode == 5 /* WINDOWING_MODE_FREEFORM */
                || taskInfo.activityType == 1 /* ACTIVITY_TYPE_STANDARD */
                && taskInfo.configuration.windowConfiguration.invokeMethodAutoAs<Int>("getDisplayWindowingMode") == 5 /* WINDOWING_MODE_FREEFORM */
    }

    private fun createWindowDecoration(
        taskInfo: ActivityManager.RunningTaskInfo,
        taskSurface: SurfaceControl,
        startT: SurfaceControl.Transaction,
        finishT: SurfaceControl.Transaction
    ) {
        ALog.d("createWindowDecoration: $taskInfo")
        val windowDecoration = YAYAMFWindowDecoration(
            context,
            displayController,
            taskInfo,
            taskSurface,
            syncQueue,
            taskOrganizer,
            this
        )
        windowDecorationById[taskInfo.taskId] = windowDecoration
        windowDecoration.relayout(taskInfo, startT, finishT, true)
    }
}