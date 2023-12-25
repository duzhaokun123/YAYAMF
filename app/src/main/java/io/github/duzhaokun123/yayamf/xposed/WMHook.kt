package io.github.duzhaokun123.yayamf.xposed

import android.content.Context
import io.github.duzhaokun123.yaxh.utils.loadClass
import io.github.duzhaokun123.yaxh.utils.makePublic
import io.github.duzhaokun123.yayamf.wm.ActivityStarter
import io.github.duzhaokun123.yayamf.wm.TaskDisplayArea

object WMHook : BaseServiceHook {
    override val serviceName: String
        get() = Context.WINDOW_SERVICE

    override fun onServiceAdded(serviceClassLoader: ClassLoader) {
        loadClass("com.android.server.wm.ActivityStarter\$Request", serviceClassLoader)
            .makePublic()
        TaskDisplayArea.init(serviceClassLoader)
        ActivityStarter.init(serviceClassLoader)
    }
}