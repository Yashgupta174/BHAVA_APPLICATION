plugins {
    alias(libs.plugins.android.application)
    // alias(libs.plugins.google.services) // Removed until google-services.json is provided
}

android {
    namespace = "com.example.bhava"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.bhava"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets {
        getByName("main") {
            res {
                srcDirs("src\\main\\res", "src\\main\\res\\challenge")
            }
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ── Networking (Retrofit + OkHttp) ───────────────────────
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ── Image loading (Glide) ────────────────────────────────
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // ── Secure token storage ─────────────────────────────────
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // ── Google Sign-In ───────────────────────────────────────
    implementation(libs.google.auth)

    // ── UI Enhancements ──────────────────────────────────────
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}