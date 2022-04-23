package fear.of.god.base

import android.app.Application
import android.content.res.Configuration
import android.net.VpnManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.github.shadowsocks.Core
import com.google.firebase.FirebaseApp
import com.tencent.mmkv.MMKV
import fear.of.god.ui.HomeActivity

@RequiresApi(Build.VERSION_CODES.P)
class IApp: Application(), androidx.work.Configuration.Provider by Core {

    override fun onCreate() {
        super.onCreate()

        Core.init(this, HomeActivity::class)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        if (!getProcessName().contains(":bg")) {
            FirebaseApp.initializeApp(this)
            MMKV.initialize(this)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Core.updateNotificationChannels()
    }
}