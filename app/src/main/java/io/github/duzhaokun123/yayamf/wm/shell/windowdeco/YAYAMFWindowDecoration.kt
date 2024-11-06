package io.github.duzhaokun123.yayamf.wm.shell.windowdeco

import android.annotation.SuppressLint
import android.app.ActivityManager.RunningTaskInfo
import android.content.Context
import android.graphics.Rect
import android.os.Binder
import android.os.Handler
import android.os.Looper
import android.view.SurfaceControl
import android.view.SurfaceControlViewHost
import android.view.View
import android.view.WindowInsets
import android.window.TaskOrganizer
import android.window.WindowContainerTransaction
import com.android.wm.shell.common.DisplayController
import com.android.wm.shell.common.SyncTransactionQueue
import io.github.duzhaokun123.yaxh.utils.addModuleAssetPath
import io.github.duzhaokun123.yayamf.utils.runMain

class YAYAMFWindowDecoration(
    val context: Context,
    val displayController: DisplayController,
    val taskInfo: RunningTaskInfo,
    val taskSurface: SurfaceControl,
    val syncQueue: SyncTransactionQueue,
    val taskOrganizer: TaskOrganizer,
    private val decorViewModel: YAYAMFWindowDecorViewModel
) : AutoCloseable {
    val docorContext = context.createConfigurationContext(taskInfo.configuration).also { it.addModuleAssetPath() }
    var decorationContainerSurface: SurfaceControl? = null
    var decorationViewHost: SurfaceControlViewHost? = null
    var decorationView: View? = null
    val params = YAYAMFWindowParams()
    val topDecor = TopDecor(docorContext, taskInfo, taskSurface, displayController, params, decorViewModel, this)
    val bottomDecor = BottomDecor(docorContext, taskInfo, taskSurface, displayController, params, decorViewModel, this)
    var handler: Handler? = null
    val owner = Binder()
    override fun close() {
//        TODO()
    }

    fun relayout(taskInfo: RunningTaskInfo) {
//        TODO("Not yet implemented")
        val t = SurfaceControl.Transaction()
        relayout(taskInfo, t, t)
        updateParams(t)
    }

    fun relayout(
        taskInfo: RunningTaskInfo,
        startT: SurfaceControl.Transaction,
        finishT: SurfaceControl.Transaction,
        applyStartTransactionOnDraw: Boolean
    ) {
//        TODO()
        relayout(taskInfo, startT, finishT)
    }

    fun relayout(
        taskInfo: RunningTaskInfo,
        startT: SurfaceControl.Transaction,
        finishT: SurfaceControl.Transaction,
    ) {
        if (handler == null) handler = Handler::class.java.getDeclaredConstructor(Looper::class.java).newInstance(
            Looper.myLooper())
        if (taskInfo.isVisible().not()) return

        taskInfo.configuration.windowConfiguration.bounds.apply {
            params.width = width()
            params.taskHeight = height()
            params.x = left
            params.y = top
        }

        handler!!.post {
            topDecor.init(finishT)
            bottomDecor.init(finishT)
            topDecor.updateParams(startT)
            topDecor.relayout(taskInfo)
            bottomDecor.updateParams(startT)
            updateParams(startT)
        }
    }

    fun postRelayout(taskInfo: RunningTaskInfo) {
        handler?.post { relayout(taskInfo) }
    }

    @SuppressLint("MissingPermission")
    private fun updateParams(t: SurfaceControl.Transaction) {
        t.setPosition(taskSurface, params.x.toFloat(), params.y.toFloat())
//        t.setPosition(decorationContainerSurface, 0F, 0F)
        t.setCrop(taskSurface, Rect(0, 0, params.width, params.height))
        topDecor.updateParams(t)
        bottomDecor.updateParams(t)
        if (params.moving) return

        val wct = WindowContainerTransaction()
        val token = taskInfo.token
        val taskBounds = Rect(0,0, params.width, params.taskHeight)
        taskBounds.offsetTo(params.x, params.y)
        wct.setBounds(token, taskBounds)
        val captionRect = Rect(0, 0, params.width, params.topDecorHeight)
        wct.addInsetsSource(token, owner, 0, WindowInsets.Type.captionBar(), captionRect, emptyArray<Rect>())
        wct.addInsetsSource(token, owner, 0, WindowInsets.Type.statusBars(), captionRect, emptyArray<Rect>())
        taskOrganizer.applyTransaction(wct)
    }

    fun updateParams() {
        val t = SurfaceControl.Transaction()
        updateParams(t)
        syncQueue.runInSync {
            it.merge(t)
            t.close()
        }
    }
}