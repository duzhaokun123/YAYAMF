package io.github.duzhaokun123.yayamf.wm.shell.windowdeco

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceControl
import android.view.View
import com.android.wm.shell.common.DisplayController
import io.github.duzhaokun123.yayamf.databinding.BottomDecorBinding
import io.github.duzhaokun123.yayamf.R

class BottomDecor(
    context: Context,
    taskInfo: ActivityManager.RunningTaskInfo,
    taskSurface: SurfaceControl,
    displayController: DisplayController,
    params: YAYAMFWindowParams,
    decorViewModel: YAYAMFWindowDecorViewModel,
    decoration: YAYAMFWindowDecoration
) : BaseDecor(
    context, taskInfo, taskSurface, displayController, params, decorViewModel, decoration
) {
    override val rect: Rect
        get() = Rect(0, 0, params.width, context.resources.getDimensionPixelSize(R.dimen.bottom_decor_height))
    override val position: PointF
        get() = PointF(0F, params.height - context.resources.getDimensionPixelSize(R.dimen.bottom_decor_height).toFloat())
    override val contentView: View
        get() = binding.root

    val binding: BottomDecorBinding = BottomDecorBinding.inflate(LayoutInflater.from(context))

    init {
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
        binding.tvResize.setOnTouchListener(resizeHandler)
    }
}