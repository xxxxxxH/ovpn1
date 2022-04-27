package fear.of.god.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.github.shadowsocks.R
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.net.HttpsTest
import com.github.shadowsocks.utils.StartService
import fear.of.god.base.BaseActivity
import fear.of.god.event.IEvent
import fear.of.god.tools.*
import fear.of.god.view.AdViewNative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class HomeActivity:BaseActivity(R.layout.layout_home) {

    private lateinit var homeStatus:TextView
    private lateinit var btn:Button
    private lateinit var ad1:AdViewNative
    private lateinit var ad2:AdViewNative
    private lateinit var image:ImageView
    private lateinit var back:ImageView
    private lateinit var title:TextView
    private lateinit var anim:LottieAnimationView
    private var connect: ActivityResultLauncher<*>? = null
    private val connection = ShadowsocksConnection(true)
    var tester: HttpsTest? = null
    var statusExtra = ""

    override fun initView() {
        super.initView()
//        loadingView.show(supportFragmentManager, "")
        EventBus.getDefault().register(this)
        tester = ViewModelProvider(this).get(HttpsTest::class.java)
        connect = registerForActivityResult(
            StartService()
        ) { result: Boolean? -> }
        connection.connect(this, SSManager.get())
        homeStatus = findViewById(R.id.homeStatus)
        ad1 = findViewById(R.id.ad1)
        ad2 = findViewById(R.id.ad2)
        image = findViewById(R.id.image)
        back = findViewById(R.id.back)
        anim = findViewById(R.id.anim)
        anim.visibility = View.VISIBLE
        back.visibility = View.GONE
        findViewById<TextView>(R.id.title).apply {
            text = "VPN"
        }
        homeStatus.setOnClickListener {
            startActivity(Intent(this, NodeActivity::class.java))
        }
        btn = findViewById(R.id.btn)
        btn.setOnClickListener {
            if (serverEntity == null){
                showToast("please select node")
            }else{
                if ((it as Button).contentDescription == "Disconnect"){
//                    loadingView.show(supportFragmentManager, "")
                    anim.visibility = View.VISIBLE
                    lifecycleScope.launch(Dispatchers.IO){
                        delay(1000)
                        withContext(Dispatchers.Main){
                            SSManager.get().stopConnect(true)
                        }
                    }
                }else if ((it as Button).contentDescription == "Connect"){
//                    loadingView.show(supportFragmentManager, "")
                    anim.visibility = View.VISIBLE
                    lifecycleScope.launch(Dispatchers.IO){
                        delay(2000)
                        withContext(Dispatchers.Main){
                            SSManager.get().startConnect(this@HomeActivity, connect!!)
                        }
                    }
                }
            }
        }
        getServers {
            it?.let {
                serverEntity = it[0]
                serverEntity?.let {s->
                    homeStatus.text = s.name
                }?: kotlin.run {
                    setStatus(Status.NONODE)
                }
            }
        }
        anim.visibility = View.GONE
    }


    private fun setStatus(status: Status){
        when(status){
            Status.NONODE->{
                homeStatus.text = "please select node"
            }
            Status.UNCONNECT->{
                btn.contentDescription = "Connect"
                btn.setBackgroundResource(R.drawable.button_connect)
                image.visibility = View.GONE
            }
            Status.CONNECTING -> {
                btn.contentDescription = "Connecting"
            }
            Status.CONNECTED->{
                btn.contentDescription = "Disconnect"
                btn.setBackgroundResource(R.drawable.button_disconnect)
                image.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SSManager.get().stopConnect(false)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e:IEvent){
        val msg = e.getMessage()
        when(msg[0]){
            "switchServer" -> {
                SSManager.get().stopConnect(false)
                anim.visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.IO){
                    delay(1000)
                    withContext(Dispatchers.Main){
                        SSManager.get().startConnect(this@HomeActivity, connect!!)
                        serverEntity?.let {
                            homeStatus.text = it.name
                        }?: kotlin.run {
                            setStatus(Status.NONODE)
                        }
                    }
                }
            }
            "connecting" -> {
                "connecting".log()
                setStatus(Status.CONNECTING)
            }
            "Connected" -> {
                "Connected".log()
                setStatus(Status.CONNECTED)
//                loadingView.dismiss()
                anim.visibility = View.GONE
                tester?.test(this){
                    ad1.loadAd()
                    ad2.loadAd()
                }
                interstitialAd?.let {
                    configEntity?.let {
                        if ((System.currentTimeMillis() - lastInterShowTime) > (it.interval * 1000)){
                            showInter()
                        }else{
                            startActivity(Intent(this, ResultActivity::class.java).apply {
                                putExtra("status", "Connected")
                            })
                        }
                    }
                    statusExtra = "Connected"
                }?: kotlin.run {
                    startActivity(Intent(this, ResultActivity::class.java).apply {
                        putExtra("status", "Connected")
                    })
                }
            }
            "Stopped" -> {
                "Stopped".log()
                anim.visibility = View.GONE
                val showAd = msg[1] as Boolean
                if (showAd){
                    setStatus(Status.UNCONNECT)
                    interstitialAd?.let {
                        configEntity?.let {
                            if ((System.currentTimeMillis() - lastInterShowTime) > (it.interval * 1000)){
                                showInter()
                            }else{
                                startActivity(Intent(this, ResultActivity::class.java).apply {
                                    putExtra("status", "Stopped")
                                })
                            }
                        }
                        statusExtra = "Stopped"
                    }?: kotlin.run {
                        startActivity(Intent(this, ResultActivity::class.java).apply {
                            putExtra("status", "Stopped")
                        })
                    }
                }
            }
            "inter dismiss" -> {
                startActivity(Intent(this, ResultActivity::class.java).apply {
                    putExtra("status", statusExtra)
                })
            }
            "start connect failed"->{
//                loadingView.dismiss()
                anim.visibility = View.GONE
            }
            "stop connected failed"->{
//                loadingView.dismiss()
                anim.visibility = View.GONE
            }
        }
    }
}