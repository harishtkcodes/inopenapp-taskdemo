/* Git: Empty commit marker \0 */
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.nav.safeargs)
}

object Ext {
    const val versionMajor = 0 // Major
    const val versionMinor = 1 // Minor
    const val versionPatch = 1 // Patches, updates
    val versionClassifier: String? = null
    const val versionRevision = "revision-06"
    const val prodRevision = "rc-01"
    const val isSnapshot = false
    const val minSdk = 26
    const val targetSdk = 34
}
android {
    namespace = "com.example.taskdemo"
    compileSdk = Ext.targetSdk

    defaultConfig {
        applicationId = "com.example.taskdemo"
        minSdk = Ext.minSdk
        targetSdk = Ext.targetSdk
        versionCode = generateVersionCode()
        versionName = generateVersionName()
        resourceConfigurations += setOf<String>("en")

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"

        freeCompilerArgs +=
            arrayOf(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
                "-Xjvm-default=all-compatibility"
            )
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    flavorDimensions.add("default")
    productFlavors {
        create("dev") {
            dimension = "default"

            buildConfigField("String", "BASE_URL", "\"https://api.inopenapp.com\"")
            buildConfigField("String", "API_URL", "\"https://api.inopenapp.com/api/v1/\"")
            buildConfigField("String", "ENV", "\"dev\"")
            buildConfigField("boolean", "IS_SECURED", "false")
            versionNameSuffix = "-dev${Ext.versionRevision}"
        }

        create("internal") {
            dimension = "default"

            buildConfigField("String", "BASE_URL", "\"https://api.inopenapp.com\"")
            buildConfigField("String", "API_URL", "\"https://api.inopenapp.com/api/v1/\"")
            buildConfigField("String", "ENV", "\"internal\"")
            buildConfigField("boolean", "IS_SECURED", "false")
            versionNameSuffix = "-internal"
        }

        create("prod") {
            dimension = "default"

            buildConfigField("String", "BASE_URL", "\"https://api.inopenapp.com\"")
            buildConfigField("String", "API_URL", "\"https://api.inopenapp.com/api/v1/\"")
            buildConfigField("String", "ENV", "\"prod\"")
            buildConfigField("boolean", "IS_SECURED", "true")
        }
    }
    lint {
        abortOnError = false
    }
}

dependencies {

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.asynclayoutinflater)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.metrics.performance)
    implementation(libs.androidx.window)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.androidx.recyclerview)

    /* Google */
    implementation(libs.google.material)

    /* Kotlinx Coroutines */
    implementation(libs.kotlinx.coroutines.android)
    /* Kotlinx Serialization */
    implementation(libs.kotlinx.serialization.json)

    /* Hilt */
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation)
    ksp(libs.hilt.android.compiler)

    /* Room Persistence */
    implementation(libs.bundles.room)

    /* Navigation Components */
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.navigation.ui.ktx)
    implementation(libs.androidx.hilt.navigation)
    implementation(libs.androidx.hilt.navigation.fragment)

    androidTestImplementation(libs.androidx.navigation.testing)

    /* Lifecycle */
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.common)
    implementation(libs.androidx.lifecycle.lifecycle.process)
    implementation(libs.androidx.lifecycle.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.extensions)

    // Work
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    /* Retrofit */
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp3.logging.interceptor)

    /* Glide */
    implementation(libs.glide)
    implementation(libs.glide.transformations)
    implementation(libs.glide.okhttp3.integration) {
        exclude("glide-parent")
    }
    ksp(libs.glide.compiler)

    /* Facebook Shimmer */
    implementation(libs.facebook.shimmer)

    /* Timber */
    implementation(libs.timber)

    /* Time Convertor */
    implementation(libs.threetenabp)

    implementation(libs.kotlinx.datetime)

    /* Event Bus */
    implementation(libs.eventbus)

    // implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    /* External Projects */
    implementation(project(":external:MPChartLib"))
    /* END - External Projects */

    // Detect memory leaks
    debugImplementation(libs.leakcanary)

    // Core library
    testImplementation(libs.junit)
    testImplementation(testLibs.androidx.testing)
    testImplementation(testLibs.androidx.arch.testing)
    testImplementation(testLibs.androidx.fragment.testing)

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation(testLibs.androidx.test.runner)
    androidTestImplementation(testLibs.androidx.test.rules)

    // Assertions
    androidTestImplementation(testLibs.androidx.test.junitext)
    androidTestImplementation(testLibs.androidx.test.truthext)
    testImplementation(testLibs.googletruth)

    // Espresso dependencies
    androidTestImplementation(testLibs.androidx.test.espressoCore)
    androidTestImplementation(testLibs.androidx.test.espressoContrib)
    androidTestImplementation(testLibs.androidx.test.espressoIntents)
    androidTestImplementation(testLibs.androidx.test.espressoAccessibility)
    androidTestImplementation(testLibs.androidx.test.espressoWeb)
    androidTestImplementation(testLibs.androidx.test.espressoConcurrent)
    androidTestImplementation(testLibs.androidx.test.espressoIdlingResource)

    // Hilt testing
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    // mockk
    testImplementation(testLibs.mock)

    // Coroutines testing
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.threetenabp.test) {
        exclude(group = "com.jakewharton.threetenabp", module = "threetenabp")
    }
}

@SuppressWarnings("GrMethodMayBeStatic")
fun generateVersionCode(): Int {
    return Ext.minSdk * 10000000 + Ext.versionMajor * 10000 + Ext.versionMinor * 100 + Ext.versionPatch
}

@SuppressWarnings("GrMethodMayBeStatic")
fun generateVersionName(): String {
    var versionName: String = "${Ext.versionMajor}.${Ext.versionMinor}.${Ext.versionPatch}"
    var versionClassifier: String? = Ext.versionClassifier
    if (Ext.versionClassifier == null && Ext.isSnapshot) {
        versionClassifier = Ext.prodRevision
    }

    if (versionClassifier != null) {
        versionName += "-" + Ext.versionClassifier
    }
    return versionName
}