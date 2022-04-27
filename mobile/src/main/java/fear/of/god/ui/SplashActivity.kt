package fear.of.god.ui

import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.github.shadowsocks.R
import fear.of.god.base.BaseActivity
import fear.of.god.tools.getConfig
import fear.of.god.tools.initFaceBook

class SplashActivity : BaseActivity(R.layout.layout_splash) {

    override fun initView() {
        super.initView()
        getConfig {
            initFaceBook()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}