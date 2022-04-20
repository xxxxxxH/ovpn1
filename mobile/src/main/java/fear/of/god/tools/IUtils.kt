package fear.of.god.tools

import android.util.Log
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
    FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds((20 * 60).toLong()).build()
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
        val servers: ArrayList<ServerEntity>? = Gson().fromJson(it, object : TypeToken<List<ServerEntity>>() {}.type)
        servers?.log("servers")
        result(servers)
    }
}

var configEntity
    get() = MMKV.defaultMMKV().decodeParcelable(Constant.KEY_CONFIG, ConfigEntity::class.java)
    set(value) {
        MMKV.defaultMMKV().encode(Constant.KEY_CONFIG, value)
    }

fun Any.log(explain:String = TAG) {
    Log.e(TAG, "$explain = ${this}")
}

lateinit var interAds:ArrayList<String>

lateinit var nativeAds:ArrayList<String>

