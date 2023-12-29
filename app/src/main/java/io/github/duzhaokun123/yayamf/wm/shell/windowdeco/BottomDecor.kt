package io.github.duzhaokun123.yayamf.wm.shell.windowdeco

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.Display
import android.view.MotionEvent
import android.view.SurfaceControl
import android.view.SurfaceControlViewHost
import android.view.View
import android.view.WindowManager
import android.view.WindowlessWindowManager
import com.android.wm.shell.common.DisplayController
import io.github.duzhaokun123.yaxh.utils.findConstructor

class BottomDecor(
    val context: Context,
    val taskInfo: ActivityManager.RunningTaskInfo,
    val taskSurface: SurfaceControl,
    val displayController: DisplayController,
    val params: YAYAMFWindowParams,
    val decorViewModel: YAYAMFWindowDecorViewModel,
    val decoration: YAYAMFWindowDecoration
) {
    val decorationContainerSurface: SurfaceControl
    val decorationViewHost: SurfaceControlViewHost
    var inited = false
    init {
        decorationContainerSurface = SurfaceControl.Builder()
            .setName("YAYAMF:BottomDecor(${taskInfo.taskId})")
            .setContainerLayer()
            .setParent(taskSurface)
            .build()
        decorationViewHost = SurfaceControlViewHost::class.java
                .findConstructor {
                    parameterTypes contentEquals arrayOf(
                        Context::class.java,
                        Display::class.java,
                        WindowlessWindowManager::class.java,
                        String::class.java
                    ) || parameterTypes contentEquals arrayOf(
                        Context::class.java,
                        Display::class.java,
                        WindowlessWindowManager::class.java
                    )
                }.let {
                    val display = displayController.getDisplay(taskInfo.displayId)
                    val windowlessWindowManager = WindowlessWindowManager(
                        taskInfo.configuration, decorationContainerSurface, null
                    )
                    if (it.parameterCount == 3) {
                        it.newInstance(context, display, windowlessWindowManager)
                    } else {
                        it.newInstance(context, display, windowlessWindowManager, "YAYAMF:BottomDecor(${taskInfo.taskId})")
                    }
                } as SurfaceControlViewHost
        val view = View(context).apply {
            setBackgroundColor(Color.RED)
                alpha = 0.5F
        }
        val resizeHandler = object : View.OnTouchListener {
            var startWidth = 0
            var startHeight = 0
            var downX = 0F
            var downY = 0F

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when(event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startWidth = params.width
                        startHeight = params.taskHeight
                        downX = event.rawX
                        downY = event.rawY
                        params.moving = true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val offsetX = (event.rawX - downX).toInt()
                        val offsetY = (event.rawY - downY).toInt()
                        params.width = (startWidth + offsetX)
                        params.taskHeight = (startHeight + offsetY)
                        decoration.updateParams()
                    }

                    MotionEvent.ACTION_UP -> {
                        params.width = (startWidth + (event.rawX - downX)).toInt()
                        params.taskHeight = (startHeight + (event.rawY - downY)).toInt()
                        params.moving = false
                        decoration.updateParams()
                    }
                }
                return true
            }
        }
        view.setOnTouchListener(resizeHandler)
        decorationViewHost.setView(view, 50, 50)
    }

    fun init(t: SurfaceControl.Transaction) {
        if (inited) return
        inited = true
        t.show(decorationContainerSurface)
        t.setTrustedOverlay(decorationContainerSurface, true)
        t.setLayer(decorationContainerSurface, WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW) // higher than task
    }

    fun updateParams(t: SurfaceControl.Transaction) {
        t.setCrop(decorationContainerSurface, Rect(0, 0, 50, 50))
        t.setPosition(decorationContainerSurface, params.width - 50F, params.height - 50F)
    }
}