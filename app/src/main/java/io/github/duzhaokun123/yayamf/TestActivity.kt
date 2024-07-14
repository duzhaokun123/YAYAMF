package io.github.duzhaokun123.yayamf

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityOptions
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import com.topjohnwu.superuser.Shell
import io.github.duzhaokun123.yayamf.utils.WINDOWING_MODE_YAYAMF
import java.io.File
import kotlin.random.Random

class TestActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        findViewById<Button>(R.id.btn1).setOnClickListener {
            val options = ActivityOptions.makeBasic()
            options.launchWindowingMode = WINDOWING_MODE_YAYAMF
            startActivity(intent, options.toBundle())
//            setTaskDescription(ActivityManager.TaskDescription.Builder()
//                .setLabel(Random.nextInt().toString())
//                .build())
        }
        findViewById<Button>(R.id.btn2).setOnClickListener {
            finish()
        }
        findViewById<Switch>(R.id.switch1).apply {
            isChecked = Shell.cmd("[ -f /data/user_de/0/com.android.systemui/ignore_crash_close ]").exec().isSuccess
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (Shell.cmd("touch /data/user_de/0/com.android.systemui/ignore_crash_close").exec().isSuccess.not()) {
                        setChecked(false)
                    }
                } else {
                    if (Shell.cmd("rm /data/user_de/0/com.android.systemui/ignore_crash_close").exec().isSuccess.not()) {
                        setChecked(true)
                    }
                }
            }
        }
    }
}