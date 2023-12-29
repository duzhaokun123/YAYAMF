package io.github.duzhaokun123.yayamf.wm.shell.windowdeco

data class YAYAMFWindowParams(
    var x: Int = 0,
    var y: Int = 0,
    var width: Int = 0,
    var taskHeight: Int = 0,
    var topDecorHeight: Int = 0,
    var moving: Boolean = false,
) {
    val height: Int
        get() = taskHeight + topDecorHeight
}