package io.github.duzhaokun123.yayamf.wm.shell.windowdeco

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.android.wm.shell.windowdecor.TaskFocusStateConsumer

class YAYAMFWindowDecorLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), TaskFocusStateConsumer {
    private var focused = false

    override fun setTaskFocusState(focused: Boolean) {
        this.focused = focused
        refreshDrawableState()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        if (focused.not())
            return super.onCreateDrawableState(extraSpace)
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        mergeDrawableStates(drawableState, intArrayOf(android.R.attr.state_focused))
        return drawableState
    }
}