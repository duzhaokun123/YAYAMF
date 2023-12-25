package io.github.duzhaokun123.yayamf.wm.shell.windowdeco

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.view.Display
import android.view.MotionEvent
import android.view.SurfaceControl
import android.view.SurfaceControlViewHost
import android.view.View
import android.view.ViewGroup
import android.view.WindowlessWindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.window.TaskOrganizer
import android.window.WindowContainerTransaction
import com.android.wm.shell.common.DisplayController
import com.android.wm.shell.common.SyncTransactionQueue
import io.github.duzhaokun123.yaxh.utils.findConstructor

class YAYAMFWindowDecoration(
    val context: Context,
    val displayController: DisplayController,
    val taskInfo: ActivityManager.RunningTaskInfo,
    val taskSurface: SurfaceControl,
    val syncQueue: SyncTransactionQueue,
    val taskOrganizer: TaskOrganizer,
    private val decorViewModel: YAYAMFWindowDecorViewModel
) : AutoCloseable {
    var decorationContainerSurface: SurfaceControl? = null
    var decorationViewHost: SurfaceControlViewHost? = null
    var decorationView: View? = null
    val params = YAYAMFWindowParams()
    override fun close() {
//        TODO()
    }

    fun relayout(taskInfo: ActivityManager.RunningTaskInfo) {
//        TODO("Not yet implemented")
        val t = SurfaceControl.Transaction()
        relayout(taskInfo, t, t)
    }

    fun relayout(
        taskInfo: ActivityManager.RunningTaskInfo,
        startT: SurfaceControl.Transaction,
        finishT: SurfaceControl.Transaction,
        applyStartTransactionOnDraw: Boolean
    ) {
//        TODO()
        relayout(taskInfo, startT, finishT)
    }

    fun relayout(
        taskInfo: ActivityManager.RunningTaskInfo,
        startT: SurfaceControl.Transaction,
        finishT: SurfaceControl.Transaction,
    ) {
        if (taskInfo.isVisible().not()) return

        taskInfo.configuration.windowConfiguration.bounds.apply {
            params.width = width()
            params.height = height()
        }
        taskInfo.positionInParent.apply {
            params.x = x
            params.y = y
        }

        if (decorationContainerSurface == null) {
            decorationContainerSurface = SurfaceControl.Builder()
                .setName("decorationContainerSurface for ${taskInfo.taskId}")
                .setContainerLayer()
                .setParent(taskSurface)
                .build()
            startT.apply {
                setTrustedOverlay(decorationContainerSurface!!, true)
                setLayer(decorationContainerSurface!!, 3 * 10000) // FIXME: why 3 * 10000?
                show(decorationContainerSurface!!)
            }
        }

        if (decorationViewHost == null) {
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
                        it.newInstance(context, display, windowlessWindowManager, "untracked")
                    }
                } as SurfaceControlViewHost
            if (decorationView == null) {
                decorationView = FrameLayout(context).apply {
                    setBackgroundColor(Color.BLUE)
                    addView(TextView(context).apply {
                        setBackgroundColor(Color.RED)
                        text = "close"
                        setOnClickListener {
                        decorViewModel.taskOperations.closeTask(taskInfo.token)
//                        decorViewModel.taskOperations.maximizeTask(taskInfo)
                    }
                    }, ViewGroup.LayoutParams(100, 100))
                    setOnTouchListener(moveHandler)
                }
            }
            decorationViewHost!!.setView(decorationView!!, 200, 100)
        }

    }

    @SuppressLint("MissingPermission")
    private fun applyParams(t: SurfaceControl.Transaction) {
        t.setPosition(taskSurface, params.x.toFloat(), params.y.toFloat())
        t.setPosition(decorationContainerSurface, 0F, 0F)
        t.setCrop(taskSurface, Rect(0, 0, params.width, params.height))
        if (params.moving) return

        val wct = WindowContainerTransaction()
        val token = taskInfo.token
        val taskBounds = Rect(0,0, params.width, params.height)
        taskBounds.offsetTo(params.x, params.y)
        wct.setBounds(token, taskBounds)
        taskOrganizer.applyTransaction(wct)
    }

    private fun applyParams() {
        val t = SurfaceControl.Transaction()
        applyParams(t)
        syncQueue.runInSync {
            it.merge(t)
            t.close()
        }
    }

    val moveHandler = object : View.OnTouchListener {
        var startX = 0
        var startY = 0
        var lastX = 0F
        var lastY = 0F
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = params.x
                    startY = params.y
                    lastX = event.rawX
                    lastY = event.rawY
                    params.moving = true
                }

                MotionEvent.ACTION_MOVE -> {
                    params.x = (startX + (event.rawX - lastX)).toInt()
                    params.y = (startY + (event.rawY - lastY)).toInt()
                    applyParams()
                }

                MotionEvent.ACTION_UP -> {
                    params.moving = false
                    applyParams()
                }
            }
            return true
        }

    }
}