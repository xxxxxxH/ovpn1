package fear.of.god.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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

@SuppressLint("SetTextI18n")
class HomeActivity:BaseActivity(R.layout.layout_home) {

    private lateinit var homeStatus:TextView
    private lateinit var btn:Button
    private lateinit var ad1:AdViewNative
    private lateinit var ad2:AdViewNative
    private var connect: ActivityResultLauncher<*>? = null
    private val connection = ShadowsocksConnection(true)
    var tester: HttpsTest? = null
    var statusExtra = ""

    override fun initView() {
        super.initView()
        EventBus.getDefault().register(this)
        tester = ViewModelProvider(this).get(HttpsTest::class.java)
        connect = registerForActivityResult(
            StartService()
        ) { result: Boolean? -> }
        connection.connect(this, SSManager.get())
        homeStatus = findViewById(R.id.homeStatus)
        ad1 = findViewById(R.id.ad1)
        ad2 = findViewById(R.id.ad2)
        homeStatus.setOnClickListener {
            startActivity(Intent(this, NodeActivity::class.java))
        }
        btn = findViewById(R.id.btn)
        btn.setOnClickListener {
            if (serverEntity == null){
                showToast("please select node")
            }else{
                if ((it as Button).text == "Disconnect"){
                    loadingView.show(supportFragmentManager, "")
                    lifecycleScope.launch(Dispatchers.IO){
                        delay(1000)
                        withContext(Dispatchers.Main){
                            SSManager.get().stopConnect()
                        }
                    }
                }else if ((it as Button).text == "Connect"){
                    loadingView.show(supportFragmentManager, "")
                    lifecycleScope.launch(Dispatchers.IO){
                        delay(2000)
                        withContext(Dispatchers.Main){
                            SSManager.get().startConnect(this@HomeActivity, connect!!)
                        }
                    }
                }
            }
        }
        serverEntity?.let {
            homeStatus.text = it.name
        }?: kotlin.run {
            setStatus(Status.NONODE)
        }
    }


    private fun setStatus(status: Status){
        when(status){
            Status.NONODE->{
                homeStatus.text = "please select node"
            }
            Status.UNCONNECT->{
                btn.text = "Connect"
            }
            Status.CONNECTING -> {
                btn.text = "Connecting"
            }
            Status.CONNECTED->{
                btn.text = "Disconnect"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e:IEvent){
        val msg = e.getMessage()
        when(msg[0]){
            "switchServer" -> {
//                SSManager.get().switchServer(this)
                serverEntity?.let {
                    homeStatus.text = it.name
                }?: kotlin.run {
                    setStatus(Status.NONODE)
                }
            }
            "connecting" -> {
                "connecting".log()
                setStatus(Status.CONNECTING)
            }
            "Connected" -> {
                "Connected".log()
                setStatus(Status.CONNECTED)
                loadingView.dismiss()
                tester?.test(this){
                    ad1.loadAd()
                    ad2.loadAd()
                }
                interstitialAd?.let {
                    showInter()
                    statusExtra = "Connected"
                }?: kotlin.run {
                    startActivity(Intent(this, ResultActivity::class.java).apply {
                        putExtra("status", "Connected")
                    })
                }
            }
            "Stopped" -> {
                "Stopped".log()
                setStatus(Status.UNCONNECT)
                loadingView.dismiss()
                interstitialAd?.let {
                    showInter()
                    statusExtra = "Stopped"
                }?: kotlin.run {
                    startActivity(Intent(this, ResultActivity::class.java).apply {
                        putExtra("status", "Stopped")
                    })
                }
            }
            "inter dismiss" -> {
                startActivity(Intent(this, ResultActivity::class.java).apply {
                    putExtra("status", statusExtra)
                })
            }
            "start connect failed"->{
                loadingView.dismiss()
            }
            "stop connected failed"->{
                loadingView.dismiss()
            }
        }
    }
}