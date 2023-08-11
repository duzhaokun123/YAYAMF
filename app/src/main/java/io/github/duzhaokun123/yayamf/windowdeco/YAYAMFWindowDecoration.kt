package io.github.duzhaokun123.yayamf.windowdeco

import android.annotation.SuppressLint
import android.app.ActivityManager.RunningTaskInfo
import android.app.TaskInfoHidden
import android.content.Context
import android.content.res.ConfigurationHidden
import android.graphics.Color
import android.graphics.Rect
import android.view.Gravity
import android.view.MotionEvent
import android.view.SurfaceControl
import android.view.SurfaceControlTransactionHidden
import android.view.SurfaceControlViewHost
import android.view.SurfaceControlViewHostHidden
import android.view.View
import android.view.WindowManager
import android.view.WindowlessWindowManager
import android.widget.FrameLayout
import android.window.WindowContainerTransaction
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.invokeMethod
import com.github.kyuubiran.ezxhelper.utils.invokeMethodAuto
import com.github.kyuubiran.ezxhelper.utils.invokeMethodAutoAs
import com.github.kyuubiran.ezxhelper.utils.loadClass
import io.github.duzhaokun123.yayamf.R
import java.lang.reflect.Proxy

class YAYAMFWindowDecoration(
    val context: Context,
    val displayController: Any /* DisplayController */,
    val taskInfo: RunningTaskInfo,
    val taskSurface: SurfaceControl,
    val syncQueue: Any, /* SyncTransactionQueue */
    val taskOrganizer: Any, /* TaskOrganizer */
    private val decorViewModel: YAYAMFWindowDecorViewModel
) : AutoCloseable {
    companion object {
        const val TAG = "YAYAMFWindowDecoration"
        val class_TransactionRunnable =
            loadClass("com.android.wm.shell.common.SyncTransactionQueue\$TransactionRunnable")
    }

    //    var viewHost: SurfaceControlViewHost? = null
//    var decorationContainerSurface: SurfaceControl? = null
//    var captionContainerSurface: SurfaceControl? = null
    var topDecorationView: View? = null
    var topDecorationViewHost: SurfaceControlViewHost? = null
    var topDecorationContainerSurface: SurfaceControl? = null
    var bottomDecorationView: View? = null
    var bottomDecorationViewHost: SurfaceControlViewHost? = null
    var bottomDecorationContainerSurface: SurfaceControl? = null
    var backgroundColorContainerSurface: SurfaceControl? = null
    var params = YAYAMFWindowParams()

    val taskOperations
        get() = decorViewModel.taskOperations

    @SuppressLint("ClickableViewAccessibility")
    fun relayout(
        taskInfo: RunningTaskInfo,
        startT: SurfaceControl.Transaction,
        finishT: SurfaceControl.Transaction
    ) {
        if (taskInfo.isVisible.not()) return

        taskInfo as TaskInfoHidden
        startT as SurfaceControlTransactionHidden
        finishT as SurfaceControlTransactionHidden
        params.topDecorationHeight =
            InitFields.moduleRes.getDimensionPixelSize(R.dimen.top_decor_height)
        params.bottomDecorationHeight =
            InitFields.moduleRes.getDimensionPixelSize(R.dimen.bottom_decor_height)
        (taskInfo.configuration as ConfigurationHidden).windowConfiguration.bounds.let {
            params.taskHeight = it.height()
            params.width = it.width()
        }
        taskInfo.positionInParent.let {
            params.x = it.x
            params.y = it.y
        }
        if (topDecorationContainerSurface == null) {
            topDecorationContainerSurface = SurfaceControl.Builder().apply {
                setName("topDecorationContainerSurface for ${taskInfo.taskId}")
                invokeMethod("setContainerLayer")
                setParent(taskSurface)
            }.build()
            startT.apply {
                setTrustedOverlay(topDecorationContainerSurface, true)
                setLayer(
                    topDecorationContainerSurface!!,
                    3 * 10000 /* TASK_CHILD_LAYER_WINDOW_DECORATIONS */
                )
                show(topDecorationContainerSurface!!)
            }
        }
        if (bottomDecorationContainerSurface == null) {
            bottomDecorationContainerSurface = SurfaceControl.Builder().apply {
                setName("bottomDecorationContainerSurface for ${taskInfo.taskId}")
                invokeMethod("setContainerLayer")
                setParent(taskSurface)
            }.build()
            startT.apply {
                setTrustedOverlay(bottomDecorationContainerSurface, true)
                setLayer(
                    bottomDecorationContainerSurface!!,
                    3 * 10000 /* TASK_CHILD_LAYER_WINDOW_DECORATIONS */
                )
                show(bottomDecorationContainerSurface!!)
            }
        }

        if (topDecorationViewHost == null) {
            topDecorationViewHost = SurfaceControlViewHostHidden(
                context, displayController.invokeMethodAutoAs("getDisplay", taskInfo.displayId)!!,
                WindowlessWindowManager(taskInfo.configuration, topDecorationContainerSurface, null)
            ) as SurfaceControlViewHost
            if (topDecorationView == null) {
                topDecorationView = View(context).apply {
                    setBackgroundColor(Color.GREEN)
                    alpha = 0.3F
                    setOnTouchListener(onMoveListener)
                }
            }
            topDecorationViewHost!!.setView(topDecorationView!!, params.width, params.topDecorationHeight)
        }
        if (bottomDecorationViewHost == null) {
            bottomDecorationViewHost = SurfaceControlViewHostHidden(
                context, displayController.invokeMethodAutoAs("getDisplay", taskInfo.displayId)!!,
                WindowlessWindowManager(
                    taskInfo.configuration,
                    bottomDecorationContainerSurface,
                    null
                )
            ) as SurfaceControlViewHost
            if (bottomDecorationView == null) {
                bottomDecorationView = FrameLayout(context).apply {
                    setBackgroundColor(Color.BLUE)
                    setOnTouchListener(onMoveListener)
                    alpha = 0.3F
                    addView(View(context).apply {
                        setBackgroundColor(Color.RED)
                        setOnTouchListener(onResizeListener)
                    }, FrameLayout.LayoutParams(100, 100).apply {
                        gravity = Gravity.END
                    })
                }
            }
            bottomDecorationViewHost!!.setView(bottomDecorationView!!, params.width, params.bottomDecorationHeight)
        }

        applyParams(finishT)
    }

    fun relayout(taskInfo: RunningTaskInfo) {
        val t = SurfaceControl.Transaction()
        relayout(taskInfo, t, t)
        syncQueue.runInSync {
            it.merge(t)
            t.close()
        }
    }

    override fun close() {
        // TODO: Implement this method
    }

    @Synchronized
    fun applyParams(t: SurfaceControl.Transaction) {
        t.setPosition(taskSurface, params.x.toFloat(), params.y.toFloat())
        t.setPosition(topDecorationContainerSurface!!, 0F, 0F)
        t.setPosition(
            bottomDecorationContainerSurface!!, 0F, params.taskHeight.toFloat()
        )
        t.setCrop(taskSurface, Rect(0, 0, params.width, params.height))
        topDecorationViewHost!!.relayout(params.width, params.topDecorationHeight)
        bottomDecorationViewHost!!.relayout(params.width, params.bottomDecorationHeight)

        if (params.moving) return
        val wct = WindowContainerTransaction()
        val token = (taskInfo as TaskInfoHidden).token
        val taskBounds = Rect(0, 0, params.width, params.taskHeight)
        taskBounds.offsetTo(params.x, params.y)
        wct.setBounds(token, taskBounds)
        val topDecorBounds = Rect(taskBounds)
        topDecorBounds.bottom = topDecorBounds.top + params.topDecorationHeight
        wct.addRectInsetsProvider(token, topDecorBounds, intArrayOf(0, 2, 12))
        taskOrganizer.invokeMethodAuto("applyTransaction", wct)
    }

    fun applyParams() {
        val t = SurfaceControl.Transaction()
        applyParams(t)
        syncQueue.runInSync {
            it.merge(t)
            t.close()
        }
    }

    private val onMoveListener = object : View.OnTouchListener {
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

    private val onResizeListener = object : View.OnTouchListener {
        var startX = 0F
        var startY = 0F
        var startWidth = 0
        var startHeight = 0
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    startY = event.rawY
                    startWidth = params.width
                    startHeight = params.taskHeight
                    params.moving = true
                }

                MotionEvent.ACTION_MOVE -> {
                    // resize preview
                }

                MotionEvent.ACTION_UP -> {
                    params.width = (startWidth + (event.rawX - startX)).toInt()
                    params.taskHeight = (startHeight + (event.rawY - startY)).toInt()
                    params.moving = false
                    applyParams()
                }
            }
            return true
        }
    }

    /**
     * for [SyncTransactionQueue.runInSync]
     */
    private fun Any.runInSync(runnable: (SurfaceControl.Transaction) -> Unit) {
        this.invokeMethodAuto("runInSync",
            Proxy.newProxyInstance(InitFields.ezXClassLoader, arrayOf(class_TransactionRunnable))
            { _, _, args ->
                runnable(args[0] as SurfaceControl.Transaction)
            })
    }
}