package io.github.duzhaokun123.yayamf

import android.annotation.SuppressLint
import com.topjohnwu.superuser.Shell
import org.lsposed.hiddenapibypass.HiddenApiBypass

@SuppressLint("StaticFieldLeak")
lateinit var app: Application

class Application: android.app.Application() {
    init {
        // Set settings before the main shell can be created
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_MOUNT_MASTER)
                .setTimeout(10)
        )
        app = this
    }
    override fun onCreate() {
        super.onCreate()
        HiddenApiBypass.addHiddenApiExemptions("")
        Shell.getShell()
    }
}