import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    kotlin("android")
    id("com.android.application")
        id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
            id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.1.20"
}

android {

    namespace = "me.timeto.app"
    compileSdk = 35



    defaultConfig {
        applicationId = "me.timeto.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 580
        versionName = "2025.06.26"
        buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties.getProperty("SUPABASE_ANON_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    flavorDimensions += "type"
    productFlavors {
        create("base") {
            dimension = "type"
        }
        create("fdroid") {
            dimension = "type"
        }
    }

    applicationVariants.all {
        outputs.all {
            this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            outputFileName = "$name.apk"
        }
    }

    // https://github.com/Medvedev91/timeto.me/issues/84
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    // https://f-droid.org/en/docs/Reproducible_Builds/#png-crushcrunch
    packaging.resources { aaptOptions.cruncherEnabled = false }

    compileOptions.sourceCompatibility = JavaVersion.VERSION_21
    compileOptions.targetCompatibility = JavaVersion.VERSION_21

    buildFeatures.buildConfig = true


}

dependencies {
    val room_version = "2.7.2"

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    val supabase_version = "3.2.0"
    val ktor_version = "3.2.1"
    implementation(platform("io.github.jan-tennert.supabase:bom:$supabase_version"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:$ktor_version")

    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.material:material:1.8.3")
    implementation("com.google.android.material:material:1.12.0")
}
