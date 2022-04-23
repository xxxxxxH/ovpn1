package fear.of.god.ui

import android.content.Intent
import android.widget.EditText
import com.github.shadowsocks.R
import fear.of.god.base.BaseActivity
import fear.of.god.tools.getConfig
import fear.of.god.tools.initFaceBook

class SplashActivity : BaseActivity(R.layout.layout_splash) {
    override fun initView() {
        super.initView()
        loadingView.show(supportFragmentManager,"")
        getConfig {
            initFaceBook()
            startActivity(Intent(this, HomeActivity::class.java))
            loadingView.dismiss()
            finish()
        }
    }
}