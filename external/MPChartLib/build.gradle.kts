plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.github.mikephil.charting"
    compileSdk = libs.versions.compileSdk.get().toIntOrNull()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toIntOrNull()
        targetSdk = libs.versions.compileSdk.get().toIntOrNull()
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
    testOptions {
        // unitTests.returnDefaultValues = true // this prevents "not mocked" error
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    testImplementation(libs.junit)
}
