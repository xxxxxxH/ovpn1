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

    private lateinit var loading: LottieAnimationView

    override fun initView() {
        super.initView()
        loading = findViewById(R.id.loading)
        loading.visibility = View.VISIBLE
//        Glide.with(this).asGif().load(R.drawable.loading_rocket).into(loading)
//        loadingView.show(supportFragmentManager,"")
        getConfig {
            initFaceBook()
//            startActivity(Intent(this, HomeActivity::class.java))
//            loadingView.dismiss()
//            loading.visibility = View.GONE
//            finish()
        }
    }
}