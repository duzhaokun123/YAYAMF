package io.github.duzhaokun123.yayamf

import android.app.ActivityOptions
import com.topjohnwu.superuser.Shell
import io.github.duzhaokun123.yayamf.bases.BaseActivity
import io.github.duzhaokun123.yayamf.databinding.ActivityTestBinding
import io.github.duzhaokun123.yayamf.utils.TipUtil
import io.github.duzhaokun123.yayamf.utils.WINDOWING_MODE_YAYAMF

class TestActivity: BaseActivity<ActivityTestBinding>(ActivityTestBinding::class.java) {
    override fun initEvents() {
        baseBinding.btn1.setOnClickListener {
            val options = ActivityOptions.makeBasic()
            options.launchWindowingMode = WINDOWING_MODE_YAYAMF
            startActivity(intent, options.toBundle())
//            setTaskDescription(ActivityManager.TaskDescription.Builder()
//                .setLabel(Random.nextInt().toString())
//                .build())
        }
        baseBinding.btn2.setOnClickListener {
            finish()
        }
        baseBinding.switch1.apply {
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
        baseBinding.btn3.setOnClickListener {
            // restart SystemUI
            Shell.cmd("pkill -f com.android.systemui").exec()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        TipUtil.showTip(this, "onBackPressed")
    }
}