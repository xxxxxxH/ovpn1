package fear.of.god.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.github.shadowsocks.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import fear.of.god.tools.getIdIndex
import fear.of.god.tools.getRandomId
import fear.of.god.tools.log
import fear.of.god.tools.nativeAds

@SuppressLint("Recycle", "CustomViewStyleable")
class AdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        val size = context.obtainStyledAttributes(attrs, R.styleable.NativeAdView)
            .getString(R.styleable.NativeAdView_native_size)
        val adId = getRandomId()
        val loader = AdLoader.Builder(context, adId).forNativeAd {
            removeAllViews()
            var v: View? = null
            when (size) {
                "big" -> {
                    v = LayoutInflater.from(getContext()).inflate(R.layout.ad_view_big, this)
                }
                "small" -> {
                    v = LayoutInflater.from(getContext()).inflate(R.layout.ad_view_small, this)
                }
                "mini" -> {
                    v = LayoutInflater.from(getContext()).inflate(R.layout.ad_view_mini, this)
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
            }

            override fun onAdImpression() {
                super.onAdImpression()
                "NativeAd onAdImpression".log()
            }
        }).build()
        loader.loadAd(AdRequest.Builder().build())
    }
}