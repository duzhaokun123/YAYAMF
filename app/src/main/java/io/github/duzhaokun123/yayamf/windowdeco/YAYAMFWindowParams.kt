package io.github.duzhaokun123.yayamf.windowdeco

data class YAYAMFWindowParams(
    var x: Int = 0,
    var y: Int = 0,
    var width: Int = 0,
    var taskHeight: Int = 0,
    var topDecorationHeight: Int = 0,
    var bottomDecorationHeight: Int = 0,
    var moving: Boolean = false,
) {
    val height
        get() = taskHeight + bottomDecorationHeight
}