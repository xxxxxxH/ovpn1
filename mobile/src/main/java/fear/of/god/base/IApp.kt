package fear.of.god.base

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.github.shadowsocks.Core
import com.google.firebase.FirebaseApp
import com.tencent.mmkv.MMKV
import fear.of.god.ui.HomeActivity

class IApp: Application(), androidx.work.Configuration.Provider by Core {
    override fun onCreate() {
        super.onCreate()

        Core.init(this, HomeActivity::class)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        FirebaseApp.initializeApp(this)
        MMKV.initialize(this)
    }
}