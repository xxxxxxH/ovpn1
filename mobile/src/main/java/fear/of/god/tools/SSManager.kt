package fear.of.god.tools

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.Profile.Companion.findAllUrls
import com.github.shadowsocks.database.ProfileManager
import fear.of.god.event.IEvent
import org.greenrobot.eventbus.EventBus

class SSManager : ShadowsocksConnection.Callback {
    var state = BaseService.State.Idle

    companion object {
        private var i: SSManager? = null
            get() {
                field ?: kotlin.run {
                    field = SSManager()
                }
                return field
            }

        @Synchronized
        fun get(): SSManager {
            return i!!
        }
    }

    fun startConnect(context: Context, connect: ActivityResultLauncher<*>) {
        serverEntity?.let {
            try {
                connect.launch(null)
            } catch (e: Exception) {
                EventBus.getDefault().post("start connect failed")
            }

        } ?: kotlin.run {
            (context as AppCompatActivity).showToast("please select node")
        }
    }

    fun stopConnect() {
        try {
            Core.stopService()
        } catch (e: Exception) {
            EventBus.getDefault().post("stop connected failed")
        }

    }

    fun reload(context: Context, connect: ActivityResultLauncher<*>) {
        serverEntity?.let {
            if (state.canStop) {
                Core.reloadService()
            } else {
                connect.launch(null)
            }
        } ?: kotlin.run {
            (context as AppCompatActivity).showToast("please select node")
        }
    }

    fun switchServer(context: Context) {
        serverEntity?.let {
            try {
                val iterator: Iterator<*> = findAllUrls(it.shareCode, null).iterator()
                val profile: Profile = iterator.next() as Profile
                profile.name = it.name
                ProfileManager.createProfile(profile)
                Core.switchProfile(profile.id)
            } catch (e: Exception) {
                (context as AppCompatActivity).showToast("please select node")
            }
        }
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        "current state $state".log("state")
        when (state) {
            BaseService.State.Connecting -> {
                EventBus.getDefault().post(IEvent("connecting"))
            }
            BaseService.State.Connected -> {
                EventBus.getDefault().post(IEvent("Connected"))
            }
            BaseService.State.Stopped -> {
                EventBus.getDefault().post(IEvent("Stopped"))
            }
        }
    }

    override fun onServiceConnected(service: IShadowsocksService) {

    }

}