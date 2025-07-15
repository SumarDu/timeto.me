buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.10.0")
                classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")
                classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.1.20-2.0.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
