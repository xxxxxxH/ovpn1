package fear.of.god.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import fear.of.god.tools.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect

@SuppressLint("Recycle", "CustomViewStyleable")
class AdViewNative @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var size = ""

    var adId = ""

    var countDownJob: Job? = null

    var delayTime = 1000L

    var nativeAd:NativeAd? = null

    init {
        size = context.obtainStyledAttributes(attrs, R.styleable.NativeAdView)
            .getString(R.styleable.NativeAdView_native_size).toString()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        countDownJob?.log("countDownJob")
        if (hasWindowFocus) {
            delayTime = 1000L
            if (isShowNativeAd(this)) {
                startCountDown(current) {
                    loadAd()
                }
            }
        } else {
            delayTime = Long.MAX_VALUE
        }
    }

    fun loadAd() {
        adId = getNativeRandomId()
        if (adId != "") {
            val loader = AdLoader.Builder(context, adId).forNativeAd {
                nativeAd = it
                removeAllViews()
                var v: View? = null
                when (size) {
                    "big" -> {
                        v = LayoutInflater.from(context).inflate(R.layout.ad_view_big, this)
                    }
                    "small" -> {
                        v = LayoutInflater.from(context).inflate(R.layout.ad_view_small, this)
                    }
                    "mini" -> {
                        v = LayoutInflater.from(context).inflate(R.layout.ad_view_mini, this)
                    }
                }
                val view = v!!.findViewById<TemplateView>(R.id.ad)
                view.setNativeAd(it)

            }.withAdListener(object : AdListener() {
                override fun onAdClosed() {
                    super.onAdClosed()
                    "NativeAd Closed".log()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    "NativeAd onAdFailedToLoad $p0".log()
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    "NativeAd onAdOpened".log()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    "NativeAd onAdLoaded".log()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    "NativeAd onAdClicked".log()
                    nativeAdClickCount++
                    nativeAdClickCount.log("nativeAdClickCount")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    "NativeAd onAdImpression".log()
                }
            }).build()
            loader.loadAd(AdRequest.Builder().build())
        } else {
            countDownJob?.cancel()
            visibility = View.INVISIBLE
        }

    }

    fun destory(){
        countDownJob?.cancel()
        nativeAd?.destroy()
    }


    private fun startCountDown(start: Int, refresh: () -> Unit) {
        countDownJob = (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
            (start..Int.MAX_VALUE).asFlow().collect {
                delay(delayTime)
                if ((it % (configEntity!!.refreshTime)) == 0) {
                    withContext(Dispatchers.Main) {
                        refresh()
                    }
                }
            }
        }
    }
}