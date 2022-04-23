package fear.of.god.ui

import android.annotation.SuppressLint
import android.widget.TextView
import com.github.shadowsocks.R
import fear.of.god.base.BaseActivity
import fear.of.god.tools.log

class ResultActivity:BaseActivity(R.layout.layout_result) {
    private val status by lazy {
        intent.getStringExtra("status")
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()
        "$status".log()
        findViewById<TextView>(R.id.homeStatus).apply {
            status?.let {
                text = when(it){
                    "Connected" -> {
                        "Connect Success"
                    }
                    "Stopped" -> {
                        "Stop Success"
                    }
                    else -> "Unknown Status"
                }
            }?: kotlin.run {
                text= "Unknown Status"
            }
        }
    }
}