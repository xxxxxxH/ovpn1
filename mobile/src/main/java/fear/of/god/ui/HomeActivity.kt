package fear.of.god.ui

import android.widget.Button
import com.github.shadowsocks.R
import fear.of.god.base.BaseActivity
import fear.of.god.view.AdViewNative

class HomeActivity:BaseActivity(R.layout.layout_home) {

    override fun initView() {
        super.initView()
        findViewById<Button>(R.id.btn).setOnClickListener {
            showInter()
        }

    }

}