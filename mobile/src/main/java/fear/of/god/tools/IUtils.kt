package fear.of.god.tools

import android.app.Application
import android.app.Application.getProcessName
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.facebook.appevents.internal.ActivityLifecycleTracker
import com.github.shadowsocks.net.HttpsTest
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

fun AppCompatActivity.initFaceBook() {
    FacebookSdk.apply {
        configEntity?.let {
            setApplicationId(it.fbId)
            sdkInitialize(this@initFaceBook)
            ActivityLifecycleTracker.apply {
                onActivityCreated(this@initFaceBook)
                onActivityResumed(this@initFaceBook)
            }
        }
    }
}

fun getIdIndex(l: Int): Int {
    return (0 until l).random()
}

fun getNativeRandomId(): String {
    var id = ""
    if (nativeAds.size > 0) {
        lastNativeAdIdIndex ++
        if (lastNativeAdIdIndex >= (nativeAds.size)){
            lastNativeAdIdIndex = 0
        }
        id = nativeAds[lastNativeAdIdIndex]
    }
    lastNativeAdIdIndex.log("native index")
    id.log("Native id")
    return id
}

fun getInterRandomId(): String {
    var id = ""
    if (interAds.size > 0) {
        lastInterAdIdIndex ++
        if (lastInterAdIdIndex >= (interAds.size - 1)){
            lastInterAdIdIndex = 0
        }
        id = interAds[lastInterAdIdIndex]
    }
    lastInterAdIdIndex.log("inter index")
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
            view.visibility = View.INVISIBLE
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

fun AppCompatActivity.showToast(s:String){
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

fun HttpsTest.test(activity: AppCompatActivity, success:()->Unit){
    status.removeObservers(activity)
    invalidate()
    status.observe(
        activity
    ) { status ->
        status.log("status")
        if (status is HttpsTest.Status.Success) {
            success()
        }

    }
    testConnection()
}
fun Application.name():String{
    var name = ""
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        name = getProcessName()
    }
    return name
}

fun Any.log(explain: String = TAG) {
    Log.e(TAG, "$explain = ${this}")
}

var configEntity
    get() = MMKV.defaultMMKV().decodeParcelable(Constant.KEY_CONFIG, ConfigEntity::class.java)
    set(value) {
        MMKV.defaultMMKV().encode(Constant.KEY_CONFIG, value)
    }

var serverEntity
    get() = MMKV.defaultMMKV().decodeParcelable(Constant.KEY_SERVER, ServerEntity::class.java)
    set(value) {
        MMKV.defaultMMKV().encode(Constant.KEY_SERVER, value)
    }

var code = ""

var interAds: ArrayList<String> = ArrayList()

var nativeAds: ArrayList<String> = ArrayList()

var nativeAdClickCount = 0

var interAdShowCount = 0

var current = 0

var lastNativeAdIdIndex = -1

var lastInterAdIdIndex = -1


