package io.github.duzhaokun123.yayamf.windowdeco

import android.app.ActivityManager.RunningTaskInfo
import android.app.TaskInfoHidden
import android.content.Context
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceControl
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.getObject
import com.github.kyuubiran.ezxhelper.utils.getObjectAs
import com.github.kyuubiran.ezxhelper.utils.invokeMethod
import com.github.kyuubiran.ezxhelper.utils.loadClass
import java.lang.reflect.Proxy

class YAYAMFWindowDecorViewModel(val originalWindowDecorViewModel: Any) {
    companion object {
        const val TAG = "YAYAMFWindowDecorViewModel"
    }

    val proxy = Proxy.newProxyInstance(
        InitFields.ezXClassLoader,
        arrayOf(loadClass("com.android.wm.shell.windowdecor.WindowDecorViewModel"))
    ) { _, method, args ->
        Log.d(TAG, "WindowDecorViewModel: ${method.name}: ${method.parameterTypes.map { it.name }.joinToString()} -> ${method.returnType.name}")
        when (method.name) {
            "setFreeformTaskTransitionStarter" -> setFreeformTaskTransitionStarter(args[0])
            "onTaskOpening" -> onTaskOpening(
                args[0] as RunningTaskInfo,
                args[1] as SurfaceControl,
                args[2] as SurfaceControl.Transaction,
                args[3] as SurfaceControl.Transaction
            )

            "onTaskInfoChanged" -> onTaskInfoChanged(args[0] as RunningTaskInfo)
            "onTaskChanging" -> onTaskChanging(
                args[0] as RunningTaskInfo,
                args[1] as SurfaceControl,
                args[2] as SurfaceControl.Transaction,
                args[3] as SurfaceControl.Transaction
            )

            "onTaskClosing" -> onTaskClosing(
                args[0] as RunningTaskInfo,
                args[1] as SurfaceControl.Transaction,
                args[2] as SurfaceControl.Transaction
            )

            "destroyWindowDecoration" -> destroyWindowDecoration(args[0] as RunningTaskInfo)
            else -> Log.e(TAG, "YAYAMFWindowDecorViewModel: unknown method ${method.name}")
        }
    }

    val context = originalWindowDecorViewModel.getObjectAs<Context>("mContext")
    val syncQueue = originalWindowDecorViewModel.getObject("mSyncQueue")
    val displayController = originalWindowDecorViewModel.getObject("mDisplayController")
    val taskOrganizer = originalWindowDecorViewModel.getObject("mTaskOrganizer")

    val windowDecorationById = SparseArray<YAYAMFWindowDecoration>()
    var taskOperations = TaskOperations(null, context, syncQueue)

    private fun setFreeformTaskTransitionStarter(transitionStarter: Any /* FreeformTaskTransitionStarter */) {
        taskOperations = TaskOperations(transitionStarter, context, syncQueue)
    }

    private fun onTaskOpening(
        taskInfo: RunningTaskInfo, taskSurface: SurfaceControl,
        startT: SurfaceControl.Transaction, finishT: SurfaceControl.Transaction
    ): Boolean {
        if (shouldShowWindowDecor(taskInfo).not()) return false
        createWindowDecoration(taskInfo, taskSurface, startT, finishT)
        return true
    }

    private fun onTaskInfoChanged(taskInfo: RunningTaskInfo) {
        windowDecorationById[taskInfo.taskId]?.relayout(taskInfo)
    }

    private fun onTaskChanging(
        taskInfo: RunningTaskInfo, taskSurface: SurfaceControl,
        startT: SurfaceControl.Transaction, finishT: SurfaceControl.Transaction
    ) {
        if (shouldShowWindowDecor(taskInfo))
            windowDecorationById[taskInfo.taskId]?.relayout(taskInfo, startT, finishT)
                ?: createWindowDecoration(taskInfo, taskSurface, startT, finishT)
        else
            destroyWindowDecoration(taskInfo)
    }

    private fun onTaskClosing(
        taskInfo: RunningTaskInfo,
        startT: SurfaceControl.Transaction, finishT: SurfaceControl.Transaction
    ) {
        windowDecorationById[taskInfo.taskId]?.relayout(taskInfo, startT, finishT)
    }

    private fun destroyWindowDecoration(taskInfo: RunningTaskInfo) {
        windowDecorationById[taskInfo.taskId]?.close()
        windowDecorationById.remove(taskInfo.taskId)
    }

    private fun shouldShowWindowDecor(taskInfo: RunningTaskInfo): Boolean {
//        return originalWindowDecorViewModel.invokeMethodAutoAs("shouldShowWindowDecor", taskInfo)
        taskInfo as TaskInfoHidden
        return taskInfo.windowingMode == 5 /* WINDOWING_MODE_FREEFORM */
                || taskInfo.activityType == 1 /* ACTIVITY_TYPE_STANDARD */
                && taskInfo.configuration.getObject("windowConfiguration")
            .invokeMethod("getDisplayWindowingMode") == 5 /* WINDOWING_MODE_FREEFORM */
    }

    private fun createWindowDecoration(
        taskInfo: RunningTaskInfo, taskSurface: SurfaceControl,
        startT: SurfaceControl.Transaction, finishT: SurfaceControl.Transaction
    ) {
        windowDecorationById[taskInfo.taskId]?.close()
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
        windowDecoration.relayout(taskInfo, startT, finishT)
    }
}