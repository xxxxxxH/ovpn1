package fear.of.god.ui

import com.github.shadowsocks.R
import fear.of.god.base.BaseActivity
import fear.of.god.tools.getConfig

class SplashActivity : BaseActivity(R.layout.layout_splash){
    override fun initView() {
        super.initView()
        getConfig({

        })
    }
}