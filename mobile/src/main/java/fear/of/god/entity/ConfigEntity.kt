package fear.of.god.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfigEntity(
    var clickLimit: Int = 0,
    var closeAd: Boolean = false,
    var closeVersions: List<String>? = null,
    var fbId: String = "",
    var interAds: List<String>? = null,
    var interval: Int = 0,
    var nativeAds: List<String>? = null,
    var showLimit: Int = 0
):Parcelable