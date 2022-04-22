package fear.of.god.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerEntity(
    var countryCode: String = "",
    var id: Int = 0,
    var name: String = "",
    var shareCode: String = "",
    var type: Int = 0,
    var select: Boolean = false
): Parcelable