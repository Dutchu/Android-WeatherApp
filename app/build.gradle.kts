plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
}

android {
    namespace = "lol.dutchu.myweather"
    compileSdk = 35

    defaultConfig {
        applicationId = "lol.dutchu.myweather"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompiler.get()
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    //Android 12 (API 31) and higher, every PendingIntent must explicitly specify immutability or mutability via FLAG_IMMUTABLE or FLAG_MUTABLE. The error is raised inside WorkManager’s internal ForceStopRunnable class. In other words, your app is using a version of WorkManager that does not set the flag when creating the PendingIntent.
    implementation(libs.work.runtime.ktx)
    coreLibraryDesugaring(libs.android.tools.desugar)

    // Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)


    // or skip Material Design and build directly on top of foundational components
    implementation(libs.androidx.foundation)
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
    implementation(libs.androidx.ui)
    // Android Studio Preview support
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    // UI Tests
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Optional - Integration with activities
    implementation(libs.androidx.activity.compose)
    // Optional - Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
//    implementation(libs.compose.material3)
//    implementation(libs.compose.icons.core)
    implementation(libs.androidx.material)
    implementation(libs.compose.icons.extended)

    //Dagger - Hilt
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    // -- OPTIONAL: Hilt + Compose Navigation --
    // (Only if you use Jetpack Compose’s Navigation with Hilt)
    implementation(libs.hilt.navigation.compose)

    // -- OPTIONAL: Hilt + WorkManager --
    // (Only if you inject Worker classes with Hilt)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // Retrofit
    implementation(libs.retrofit.core2)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.okhttp3)
    implementation(libs.moshi)         // or latest
    ksp(libs.moshi.kotlin.codegen)   // or latest


    // Location Services
    implementation(libs.android.gms.play.location)

    //Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
