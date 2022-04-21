package fear.of.god.tools

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import fear.of.god.entity.ConfigEntity
import fear.of.god.entity.ServerEntity

val TAG = "xxxxxxH"

val firebaseRemoteConfig by lazy {
    FirebaseRemoteConfig.getInstance()
}

val configSettings by lazy {
    FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds((20 * 60).toLong())
        .build()
}


fun getConfig(nextStep: (ConfigEntity?) -> Unit) {
    firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener {
        firebaseRemoteConfig.getString(Constant.CONFIG_KEY).let {
            it.log()
            AesUtils.decrypt(it, Constant.AES_KEY)
        }?.let {
            it.log()
            configEntity = Gson().fromJson(it, ConfigEntity::class.java)
            configEntity?.log("config")
            configEntity
        }?.let {
            interAds = it.interAds as ArrayList<String>
            nativeAds = it.nativeAds as ArrayList<String>
        }
        nextStep(configEntity)
    }
}

fun getServers(result: (ArrayList<ServerEntity>?) -> Unit) {
    firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    firebaseRemoteConfig.getString(Constant.SEVER_KEY).let {
        it.log()
        AesUtils.decrypt(it, Constant.AES_KEY)
    }?.let {
        it.log()
        val servers: ArrayList<ServerEntity>? =
            Gson().fromJson(it, object : TypeToken<List<ServerEntity>>() {}.type)
        servers?.log("servers")
        result(servers)
    }
}

fun getIdIndex(l: Int): Int {
    return (0 until l).random()
}

fun getNativeRandomId(): String {
    var id = ""
    if (nativeAds.size > 0) {
        val index = getIdIndex(nativeAds.size)
        id = nativeAds[index]
        nativeAds.removeAt(index)
    }
    id.log("Native id")
    return id
}

fun getInterRandomId(): String {
    var id = ""
    if (interAds.size > 0) {
        val index = getIdIndex(interAds.size)
        id = interAds[index]
        interAds.removeAt(index)
    }
    id.log("Inter id")
    return id
}

fun AppCompatActivity.getPackageVersionCode(): Int {
    var code = 0
    try {
        code = packageManager.getPackageInfo(packageName, 0).versionCode
    } catch (e: Exception) {
        e.log()
    }
    return code
}

fun isShowNativeAd(view: View): Boolean {
    configEntity?.let {
        if (it.closeAd)
            return false
        if (nativeAdClickCount >= it.clickLimit) {
            view.visibility = View.GONE
            return false
        }
        if (it.closeVersions!!.contains(code)) {
            return false
        }
    } ?: run {
        return false
    }
    return true
}

fun isShowInterAd(): Boolean {
    configEntity?.let {
        if (it.closeAd)
            return false
        if (interAdShowCount > it.showLimit)
            return false
        if (it.closeVersions!!.contains(code))
            return false
    } ?: run {
        return false
    }
    return true
}

fun Any.log(explain: String = TAG) {
    Log.e(TAG, "$explain = ${this}")
}

var configEntity
    get() = MMKV.defaultMMKV().decodeParcelable(Constant.KEY_CONFIG, ConfigEntity::class.java)
    set(value) {
        MMKV.defaultMMKV().encode(Constant.KEY_CONFIG, value)
    }

var code = ""

var interAds: ArrayList<String> = ArrayList()

var nativeAds: ArrayList<String> = ArrayList()

var nativeAdClickCount = 0

var interAdShowCount = 0

var current = 0

