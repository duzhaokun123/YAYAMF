package io.github.duzhaokun123.yayamf.wm.shell.windowdeco

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.os.Trace
import android.view.Display
import android.view.MotionEvent
import android.view.SurfaceControl
import android.view.SurfaceControlViewHost
import android.view.View
import android.view.WindowManager
import android.view.WindowlessWindowManager
import android.window.InputTransferToken
import android.window.WindowContainerTransaction
import com.android.wm.shell.common.DisplayController
import io.github.duzhaokun123.yaxh.utils.findConstructor

abstract class BaseDecor(
    val context: Context,
    val taskInfo: ActivityManager.RunningTaskInfo,
    val taskSurface: SurfaceControl,
    val displayController: DisplayController,
    val params: YAYAMFWindowParams,
    val decorViewModel: YAYAMFWindowDecorViewModel,
    val decoration: YAYAMFWindowDecoration
) : AutoCloseable {
    val decorationContainerSurface: SurfaceControl
    val decorationViewHost: SurfaceControlViewHost
    var inited = false
    abstract val rect: Rect
    abstract val position: PointF
    abstract val contentView: View

    init {
        decorationContainerSurface = SurfaceControl.Builder()
            .setName("YAYAMF:${javaClass.simpleName}(${taskInfo.taskId})")
            .setContainerLayer()
            .setParent(taskSurface)
            .build()
        decorationViewHost = SurfaceControlViewHost::class.java
                .findConstructor {
                    parameterCount == 4 // SurfaceControlViewHost(@NonNull Context c, @NonNull Display d, @NonNull WindowlessWindowManager wwm, @NonNull String callsite)
                }.let {
                    val display = displayController.getDisplay(taskInfo.displayId)
                    val windowlessWindowManager = WindowlessWindowManager::class.java.findConstructor {
                        true // WindowlessWindowManager(Configuration c, SurfaceControl rootSurface, InputTransferToken hostInputTransferToken) {
                    }.newInstance(
                        taskInfo.configuration, decorationContainerSurface, null as InputTransferToken?,
                    )
                it.newInstance(context, display, windowlessWindowManager, "YAYAMF:${javaClass.simpleName}(${taskInfo.taskId})")
                } as SurfaceControlViewHost
    }

    fun init(t: SurfaceControl.Transaction) {
        if (inited) return
        inited = true
        t.show(decorationContainerSurface)
        t.setTrustedOverlay(decorationContainerSurface, true)
        t.setLayer(decorationContainerSurface, WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW) // higher than task
        decorationViewHost.setView(contentView, rect.width(), rect.height())
    }

    fun updateParams(t: SurfaceControl.Transaction) {
        t.setCrop(decorationContainerSurface, rect)
        t.setPosition(decorationContainerSurface, position.x, position.y)
    }

    override fun close() {
        Trace.beginSection("WindowDecoration#close")
        val wct = WindowContainerTransaction()
        decorViewModel.taskOrganizer.applyTransaction(wct)
        taskSurface.release()
        Trace.endSection()
    }
}