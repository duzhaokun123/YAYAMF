package io.github.duzhaokun123.yayamf.wm.shell.windowdeco

import android.annotation.SuppressLint
import android.app.ActivityManager.RunningTaskInfo
import android.app.IActivityTaskManager
import android.content.Context
import android.content.pm.IPackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.ServiceManager
import android.os.Trace
import android.view.Display
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceControl
import android.view.SurfaceControlViewHost
import android.view.View
import android.view.WindowManager
import android.view.WindowlessWindowManager
import android.window.InputTransferToken
import android.window.WindowContainerTransaction
import androidx.core.graphics.ColorUtils
import androidx.wear.widget.RoundedDrawable
import com.android.wm.shell.common.DisplayController
import com.google.android.material.color.MaterialColors
import io.github.duzhaokun123.yaxh.utils.findConstructor
import io.github.duzhaokun123.yaxh.utils.invokeMethodAuto
import io.github.duzhaokun123.yaxh.utils.logger.ALog
import io.github.duzhaokun123.yayamf.R
import io.github.duzhaokun123.yayamf.databinding.TopDecorBinding

@SuppressLint("ClickableViewAccessibility")
class TopDecor(
    val context: Context,
    val taskInfo: RunningTaskInfo,
    val taskSurface: SurfaceControl,
    val displayController: DisplayController,
    val params: YAYAMFWindowParams,
    val decorViewModel: YAYAMFWindowDecorViewModel,
    val decoration: YAYAMFWindowDecoration
) {
    val decorationContainerSurface: SurfaceControl
    val decorationViewHost: SurfaceControlViewHost
    val viewBinding: TopDecorBinding
    val ipm: IPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"))
    val itm = IActivityTaskManager.Stub.asInterface(ServiceManager.getService("activity_task"))
    val pm = context.packageManager
    var inited = false
    init {
        decorationContainerSurface = SurfaceControl.Builder()
            .setName("YAYAMF:TopDecor(${taskInfo.taskId})")
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
                    val windowlessWindowManager = WindowlessWindowManager::class.java.findConstructor {
                        true
                    }.newInstance(
                        taskInfo.configuration, decorationContainerSurface, null as InputTransferToken?,
                    )
                    if (it.parameterCount == 3) {
                        it.newInstance(context, display, windowlessWindowManager)
                    } else {
                        it.newInstance(context, display, windowlessWindowManager, "YAYAMF:TopDecor(${taskInfo.taskId})")
                    }
                } as SurfaceControlViewHost
        params.topDecorHeight = context.resources.getDimensionPixelSize(R.dimen.top_decor_height)
        viewBinding = TopDecorBinding.inflate(LayoutInflater.from(context))
        decorationViewHost.setView(viewBinding.root, params.width, params.topDecorHeight)
        viewBinding.ibClose.setOnClickListener {
            itm.removeTask(taskInfo.taskId)
        }
        val moveHandler = object : View.OnTouchListener {
            var startX = 0
            var startY = 0
            var downX = 0F
            var downY = 0F

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = params.x
                        startY = params.y
                        downX = event.rawX
                        downY = event.rawY
                        params.moving = true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val offsetX = (event.rawX - downX).toInt()
                        val offsetY = (event.rawY - downY).toInt()
                        params.x = (startX + offsetX)
                        params.y = (startY + offsetY)
                        decoration.updateParams()
                    }

                    MotionEvent.ACTION_UP -> {
                        params.x = (startX + (event.rawX - downX)).toInt()
                        params.y = (startY + (event.rawY - downY)).toInt()
                        params.moving = false
                        decoration.updateParams()
                    }
                }
                return true
            }
        }
        viewBinding.root.setOnTouchListener(moveHandler)
    }

    fun init(t: SurfaceControl.Transaction) {
        if (inited) return
        inited = true
        t.show(decorationContainerSurface)
        t.setTrustedOverlay(decorationContainerSurface, true)
        t.setLayer(decorationContainerSurface, WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW) // higher than task
    }

    fun updateParams(t: SurfaceControl.Transaction) {
        t.setCrop(decorationContainerSurface, Rect(0, 0, params.width, params.topDecorHeight))
        t.setPosition(decorationContainerSurface, 0F, 0F)
    }

    fun relayout(taskInfo: RunningTaskInfo) {
        decorationViewHost.relayout(params.width, params.topDecorHeight)
        val activityInfo = taskInfo.topActivityInfo ?: return
        viewBinding.tvLabel.text = "${taskInfo.taskDescription?.label ?: activityInfo.loadLabel(pm)} (${taskInfo.taskId})"
        val statusBarColor = taskInfo.taskDescription?.statusBarColor ?: 0xFF000000.toInt()
        val backgroundColor = taskInfo.taskDescription?.backgroundColor ?: 0xFF000000.toInt()
        val isLightColor = MaterialColors.isColorLight(ColorUtils.compositeColors(statusBarColor, backgroundColor))
        val onStatusBarColor = if (isLightColor) Color.BLACK else Color.WHITE // FIXME: use themed color
        viewBinding.tvLabel.setTextColor(onStatusBarColor)
        viewBinding.rlRoot.setBackgroundColor(statusBarColor)
        viewBinding.ibClose.imageTintList = ColorStateList.valueOf(onStatusBarColor)
//        val activityInfo = ipm.getActivityInfo(taskInfo.topActivity, 0, taskInfo.userId)
        val icon = runCatching { taskInfo.taskDescription?.icon }.getOrNull()?.let { BitmapDrawable(it) } ?: runCatching { activityInfo.loadIcon(pm) }.getOrNull()
        icon?.let {
            viewBinding.ivIcon.setImageDrawable(RoundedDrawable().apply {
                drawable = it
                isClipEnabled = true
                radius = 100
            })
        }
    }

    fun close() {

            Trace.beginSection("WindowDecoration#close");
        val wct = WindowContainerTransaction();
        decorViewModel.taskOrganizer.applyTransaction(wct);
        taskSurface.release();
        Trace.endSection();
    }
}