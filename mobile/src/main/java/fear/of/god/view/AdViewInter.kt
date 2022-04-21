package fear.of.god.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import fear.of.god.tools.*

class AdViewInter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val adRequest: AdRequest = AdRequest.Builder().build()
    private var interstitialAd: InterstitialAd? = null
    private val adId by lazy {
        getInterRandomId()
    }

    init {
        if (adId != "") {
            configEntity?.let {
                if (!it.closeAd) {
                    if (interAdShowCount < it.showLimit) {
                        if (!it.closeVersions!!.contains(code)) {

                        }
                        InterstitialAd.load(
                            context,
                            adId,
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdLoaded(p0: InterstitialAd) {
                                    p0.log("InterAd onAdLoaded")
                                    interstitialAd = p0
                                    interstitialAd?.fullScreenContentCallback =
                                        object : FullScreenContentCallback() {
                                            override fun onAdDismissedFullScreenContent() {
                                                "".log("onAdDismissedFullScreenContent")
                                            }

                                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                                "$adError".log("onAdFailedToShowFullScreenContent")
                                            }

                                            override fun onAdShowedFullScreenContent() {
                                                "".log("onAdShowedFullScreenContent")
                                                interstitialAd = null
                                            }
                                        }
                                    if (interstitialAd != null) {
                                        interstitialAd?.show(context as Activity)
                                        interAdShowCount++
                                    } else {
                                        "null".log("interstitialAd")
                                    }
                                }

                                override fun onAdFailedToLoad(p0: LoadAdError) {
                                    p0.log("InterAd onAdFailedToLoad")
                                }
                            })

                    }
                }
            }
        }
    }
}