import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi.plugin)
    alias(libs.plugins.detekt)
    alias(libs.plugins.deploygate)
}

android {
    namespace = "com.snowdango.sumire"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.snowdango.sumire"
        minSdk = libs.versions.minsdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val properties = readProperties(file("../siging.properties"))
        create("release") {
            storeFile = file("../release.jks")
            storePassword = properties.getProperty("release.store_pass")
            keyAlias = properties.getProperty("release.alias")
            keyPassword = properties.getProperty("release.alias_pass")
        }
        getByName("debug") {
            storeFile = file("../debug.jks")
            storePassword = properties.getProperty("debug.store_pass")
            keyAlias = properties.getProperty("debug.alias")
            keyPassword = properties.getProperty("debug.alias_pass")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    ksp {
        arg("skipPrivatePreviews", "true")
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

deploygate {
    val properties = readProperties(file("../local.properties"))
    appOwnerName = properties.getProperty("deploygate.user")
    apiToken = properties.getProperty("deploygate.token")
    deployments {
        create("release") {
            sourceFile = file("build/outputs/apk/release/app-release.apk")
        }
        create("debug") {
            sourceFile = file("build/outputs/apk/debug/app-debug.apk")
        }
    }
}

detekt {
    autoCorrect = true
    parallel = true
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    ignoreFailures = true
    // Dangerで指摘するときのためにリポジトリルートにする
    // danger-checkstyle_reportsのバグでカスタマイズできないため
    basePath = file("$rootDir/../").absolutePath
}

dependencies {
    implementation(project(":ui"))
    implementation(project(":infla"))
    implementation(project(":data"))
    implementation(project(":presenter:playing"))
    implementation(project(":presenter:history"))
    implementation(project(":presenter:settings"))
    implementation(project(":presenter:widget"))
    implementation(project(":repository"))
    implementation(project(":usecase"))
    implementation(project(":model"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashcreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.windiow)
    implementation(libs.androidx.icons)
    implementation(libs.androidx.navigation)
    implementation(libs.koin)
    implementation(libs.kotlinx.datetime)
    implementation(libs.ui.tooling)
    implementation(libs.bundles.glance)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.workmanager.ktx)

    implementation(libs.showkase)
    ksp(libs.showkase.prosessor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.robolectric)
    testImplementation(libs.bundles.roborazzi)

    detektPlugins(libs.detekt.formatting)
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}