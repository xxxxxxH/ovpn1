package fear.of.god.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import fear.of.god.tools.*

abstract class BaseActivity(id: Int) : AppCompatActivity(id) {

    private val adRequest: AdRequest = AdRequest.Builder().build()
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        createInterAd()
        if (code == "") {
            code = getPackageVersionCode().toString()
        }
    }

    open fun initView() {}

    fun showInter() {
        interstitialAd?.let {
            it.show(this)
            interAdShowCount++
        } ?: kotlin.run {
            createInterAd()
        }
    }

    private fun createInterAd() {
        val adId = getInterRandomId()
        if (adId != "") {
            if (isShowInterAd()) {
                InterstitialAd.load(
                    this,
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

                                    override fun onAdFailedToShowFullScreenContent(
                                        adError: AdError?
                                    ) {
                                        "$adError".log("onAdFailedToShowFullScreenContent")
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        "".log("onAdShowedFullScreenContent")
                                        interstitialAd = null
                                        createInterAd()
                                    }
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