package io.github.duzhaokun123.yayamf

import org.lsposed.hiddenapibypass.HiddenApiBypass

class Application: android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        HiddenApiBypass.addHiddenApiExemptions("")
    }
}