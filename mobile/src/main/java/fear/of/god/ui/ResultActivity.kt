package fear.of.god.ui

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import com.github.shadowsocks.R
import fear.of.god.base.BaseActivity
import fear.of.god.tools.log

class ResultActivity : BaseActivity(R.layout.layout_result) {
    private val status by lazy {
        intent.getStringExtra("status")
    }

    private lateinit var imageResult: ImageView

    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()
        "$status".log()
        imageResult = findViewById(R.id.imageResult)
        findViewById<ImageView>(R.id.back).apply {
            setOnClickListener { finish() }
        }
        findViewById<TextView>(R.id.title).apply {
            text = "Result"
        }
        findViewById<TextView>(R.id.homeStatus).apply {
            status?.let {
                when (it) {
                    "Connected" -> {
                        text = "Connect Success"
                        imageResult.setBackgroundResource(R.drawable.sucess)
                    }
                    "Stopped" -> {
                        text = "Stop Success"
                        imageResult.setBackgroundResource(R.drawable.fali)
                    }
                    else -> "Unknown Status"
                }
            } ?: kotlin.run {
                text = "Unknown Status"
            }
        }
    }
}