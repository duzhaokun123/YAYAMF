package io.github.duzhaokun123.yayamf.utils

import android.annotation.AttrRes
import android.content.res.Resources
import android.util.TypedValue

const val WINDOWING_MODE_FREEFORM = 5
const val WINDOWING_MODE_YAYAMF = WINDOWING_MODE_FREEFORM
//const val WINDOWING_MODE_YAYAMF = 120

fun isYayamfWindowingMode(windowingMode: Int) = windowingMode == WINDOWING_MODE_YAYAMF

fun Resources.Theme.getAttr(@AttrRes id: Int) =
    TypedValue().apply { resolveAttribute(id, this, true) }