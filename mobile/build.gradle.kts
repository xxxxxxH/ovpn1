plugins {
    id("com.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
    id("kotlin-parcelize")
}

setupApp()


android.apply {
    defaultConfig.applicationId = "com.sharp.fast.vpn"
    signingConfigs {
        create("sign") {
            storeFile = project.file("../publish/feifeivpn_peace.jks")
            storePassword = "feifeivpn"
            keyAlias = "feifeivpn"
            keyPassword = "feifeivpn"
        }
        create("test") {
            storeFile = project.file("../publish/test_key.jks")
            storePassword = "vpntest"
            keyAlias = "vpntest"
            keyPassword = "vpntest"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("sign")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("test")
        }
    }
}


dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

    val cameraxVersion = "1.0.1"

    implementation("androidx.browser:browser:1.3.0")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:1.0.0-alpha28")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("com.google.mlkit:barcode-scanning:17.0.0")
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.takisoft.preferencex:preferencex-simplemenu:1.1.0")
    implementation("com.twofortyfouram:android-plugin-api-for-locale:1.0.4")
    implementation("me.zhanghai.android.fastscroll:library:1.1.7")


    implementation("com.google.android.gms:play-services-ads:20.5.0")
    //旋转动画
    implementation("com.ldoublem.loadingview:loadingviewlib:1.0")

    implementation("com.facebook.android:facebook-android-sdk:12.2.0")

    implementation(platform("com.google.firebase:firebase-bom:29.1.0"))
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.pangle.global:ads-sdk:4.1.1.2")
    implementation("com.google.android.gms:play-services-ads-identifier:17.0.0")
    implementation("com.tencent:mmkv:1.2.13")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")
}
