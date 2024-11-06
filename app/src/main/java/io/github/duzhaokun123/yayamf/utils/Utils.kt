package io.github.duzhaokun123.yayamf.utils

import android.annotation.AttrRes
import android.app.WindowConfiguration
import android.content.res.Resources
import android.util.TypedValue
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val WINDOWING_MODE_FREEFORM = 5
const val WINDOWING_MODE_YAYAMF = WINDOWING_MODE_FREEFORM
//const val WINDOWING_MODE_YAYAMF = 120

fun isYayamfWindowingMode(windowingMode: Int) = windowingMode == WINDOWING_MODE_YAYAMF

fun Resources.Theme.getAttr(@AttrRes id: Int) =
    TypedValue().apply { resolveAttribute(id, this, true) }

fun runMain(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.Main, block = block)

fun runIO(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.IO, block = block)

val WindowInsetsCompat.maxSystemBarsDisplayCutout
    get() = getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())

val WindowInsetsCompat.maxSystemBarsDisplayCutoutIme
    get() = getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime())
