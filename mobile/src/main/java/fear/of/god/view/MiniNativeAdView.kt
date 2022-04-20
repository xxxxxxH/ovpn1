package fear.of.god.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.github.shadowsocks.R
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.nativead.NativeAdView

class MiniNativeAdView : LinearLayout {

    lateinit var nativeAdView:NativeAdView
    lateinit var background:RelativeLayout
    lateinit var primaryView:TextView
    lateinit var tertiaryView:TextView
    lateinit var iconView:ImageView
    lateinit var callToActionView:Button

    constructor(context: Context) : super(context){
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initView(context)
    }

    private fun initView(context: Context): View {
        val v = LayoutInflater.from(context).inflate(R.layout.ad_view_mini, this, true)
        nativeAdView = v.findViewById(R.id.native_ad_view)
        background = v.findViewById(R.id.background)
        primaryView = v.findViewById(R.id.primary)
        tertiaryView = v.findViewById(R.id.body)
        iconView = v.findViewById(R.id.icon)
        callToActionView = v.findViewById(R.id.cta)
        return v
    }

    fun loadAd(context: Context){
        val loader = AdLoader.Builder(context,"").forNativeAd { nativeAd->
            val store = nativeAd.store
            val advertiser = nativeAd.advertiser
            val headline = nativeAd.headline
            val body = nativeAd.body
            val cta = nativeAd.callToAction
            val starRating = nativeAd.starRating
            val icon = nativeAd.icon
            nativeAdView.callToActionView = callToActionView
            nativeAdView.headlineView = primaryView
            primaryView.text = headline
            callToActionView.text = cta
        }
    }

}