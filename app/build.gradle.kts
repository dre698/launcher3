plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.android.launcher3" // TODO: pertimbangkan ganti ke package unik
                                          // (misal io.dre698.nadekolauncher) biar gak
                                          // bentrok kalau device juga punya Launcher3
                                          // system bawaan.
    compileSdk = 36 // Android 16

    defaultConfig {
        applicationId = "com.android.launcher3"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    // Source AOSP asli dipisah src/ + src_no_quickstep/, di sini digabung
    // jadi satu source set biar gak perlu source-set kustom.
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.srcDirs(
                "src/main/java",
                "src/main/java_build_config"
            )
            res.srcDirs("src/main/res")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false // aktifkan + isi proguard.flags kalau udah stabil
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        aidl = true
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    // ---- Dependency yang tinggal resolve dari Maven, aman ----
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.slice.view)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.window)
    implementation(libs.androidx.dynamicanimation)
    implementation(libs.material)

    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
    implementation(libs.javax.inject) // pengganti jsr330

    implementation(libs.androidx.emoji2)
    implementation(libs.androidx.emoji2.emojipicker)

    // ---- Dependency yang BUTUH kerja tambahan (lihat NOTES.md) ----
    // TODO: vendor source dari frameworks/libs/systemui (iconloader_base,
    //       view_capture, animationlib, contextualeducationlib, mechanics, msdl)
    //       -> taruh sebagai module lokal, misal :vendor:iconloader
    // implementation(project(":vendor:iconloader"))

    // TODO: WindowManager-Shell-shared-AOSP -> vendor source atau hapus
    //       pemakaiannya kalau fiturnya gak dipakai (biasanya window insets util)

    // TODO: com_android_*_flags_lib (aconfig) -> stub jadi object Kotlin
    //       berisi konstanta boolean, lihat NOTES.md poin 3

    // TODO: org.lineageos.platform -> hapus/stub kalau gak target device Lineage

    // TODO: chaldea.seraphixgoogle -> source-nya ada di folder seraphixgoogle/
    //       repo asal, belum di-include ke module ini, tambahin manual kalau
    //       fitur Google Feed card mau dipertahankan

    // TODO: libGoogleFeed (libs/libGoogleFeed.jar, closed-source prebuilt)
    //       -> copy jar-nya ke app/libs/ kalau mau dipakai, atau skip fiturnya

    // ---- Testing (opsional, boleh dihapus kalau gak perlu unit test) ----
    testImplementation(libs.junit)
}

