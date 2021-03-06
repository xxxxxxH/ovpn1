package fear.of.god.ui

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.github.shadowsocks.R
import fear.of.god.adapter.NodeAdapter
import fear.of.god.base.BaseActivity
import fear.of.god.entity.ServerEntity
import fear.of.god.event.IEvent
import fear.of.god.tools.SSManager
import fear.of.god.tools.getServers
import fear.of.god.tools.serverEntity
import fear.of.god.view.AdViewNative
import org.greenrobot.eventbus.EventBus

@SuppressLint("NotifyDataSetChanged")
class NodeActivity : BaseActivity(R.layout.layout_node) {
    lateinit var recycler: RecyclerView
    lateinit var ad:AdViewNative
    private lateinit var anim: LottieAnimationView

    override fun initView() {
        super.initView()
        anim = findViewById(R.id.anim)
        anim.visibility = View.VISIBLE
        ad = findViewById(R.id.ad1)
        recycler = findViewById(R.id.recycler)
        findViewById<ImageView>(R.id.back).apply {
            setOnClickListener { finish() }
        }
        findViewById<TextView>(R.id.title).apply {
            text = "Fast Smart"
        }
        getServers {
            it?.let {d->

                serverEntity?.let { s ->
                    for (item in it) {
                        if (item.id == s.id) {
                            item.select = true
                            break
                        }
                    }
                }

                val a = NodeAdapter(d)
                recycler.layoutManager = LinearLayoutManager(this)
                recycler.adapter = a
                anim.visibility = View.GONE
                a.setOnItemClickListener { adapter, _, position ->
                    val data = adapter.data as ArrayList<ServerEntity>
                    for ((index, item) in data.withIndex()) {
                        item.select = index == position
                    }
                    adapter.notifyDataSetChanged()
                    serverEntity = data[position]
                    EventBus.getDefault().post(IEvent("switchServer"))
                    finish()
                }
            }?: kotlin.run {
                anim.visibility = View.GONE
            }

        }
    }

    override fun onDestroy() {
        ad.destory()
        super.onDestroy()
    }
}