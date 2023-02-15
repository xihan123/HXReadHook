import java.io.FileInputStream
import java.util.Properties

apply {
    from("${rootProject.projectDir}/gradle/release.gradle")
}

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "cn.xihan.hxds"
    compileSdk = rootProject.extra["targetSdkVersion"] as Int

    androidResources.additionalParameters += arrayOf(
        "--allow-reserved-package-id",
        "--package-id",
        "0x64"
    )

    signingConfigs {
        create("xihantest") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    defaultConfig {
        minSdk = rootProject.extra["minSdkVersion"] as Int
        targetSdk = rootProject.extra["targetSdkVersion"] as Int
        versionCode = rootProject.extra["appVersionCode"] as Int
        versionName = rootProject.extra["appVersionName"] as String

        resourceConfigurations.addAll(listOf("zh"))

        signingConfig = signingConfigs.getByName("xihantest")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isJniDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            isPseudoLocalesEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions.jvmTarget = "17"

    buildFeatures.compose = true

    composeOptions.kotlinCompilerExtensionVersion =
        rootProject.extra["kotlinCompilerExtensionVersion"] as String

    packagingOptions.apply {
        resources.excludes += mutableSetOf(
            "META-INF/*******",
            "**/*.txt",
            "**/*.xml",
            "**/*.properties",
            "DebugProbesKt.bin",
            "java-tooling-metadata.json",
            "kotlin-tooling-metadata.json"
        )
        dex.useLegacyPackaging = true
    }

    lint.abortOnError = false
}

dependencies {

    implementation("androidx.core:core-ktx:${rootProject.extra["coreVersion"]}")
    implementation("androidx.appcompat:appcompat:${rootProject.extra["appcompatVersion"]}")
    implementation("androidx.activity:activity-ktx:${rootProject.extra["activityVersion"]}")
    implementation("com.google.android.material:material:${rootProject.extra["materialVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${rootProject.extra["kotlinxJsonVersion"]}")
//    implementation("com.alibaba.fastjson2:fastjson2-kotlin:${rootProject.extra["fastJson2Version"]}")
//    implementation("com.github.liangjingkanji:spannable:latest.release")
//    implementation(platform("androidx.compose:compose-bom:2023.01.00"))
    implementation("dev.chrisbanes.compose:compose-bom:2023.02.00-beta01")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.activity:activity-compose:${rootProject.extra["activityVersion"]}")
    implementation("androidx.navigation:navigation-compose:${rootProject.extra["navComposeVersion"]}")
    implementation("com.google.accompanist:accompanist-systemuicontroller:${rootProject.extra["accompanistVersion"]}")
    implementation("com.google.accompanist:accompanist-themeadapter-material3:${rootProject.extra["accompanistVersion"]}")
    // https://github.com/getActivity/XXPermissions
    implementation("com.github.getActivity:XXPermissions:latest.release") {
        exclude(group = "com.android.support")
    }
    // https://github.com/skydoves/landscapist#coil
//    implementation("com.github.skydoves:landscapist-coil:${rootProject.extra["landscapistVersion"]}") {
//        exclude(group = "io.coil-kt")
//    }
//    implementation("io.coil-kt:coil-compose:${rootProject.extra["coilVersion"]}")
    // 基础依赖
    implementation("com.highcapable.yukihookapi:api:${rootProject.extra["YuKiHookVersion"]}")
    // ❗作为 Xposed 模块使用务必添加，其它情况可选
    compileOnly("de.robv.android.xposed:api:82")
    // ❗作为 Xposed 模块使用务必添加，其它情况可选
    ksp("com.highcapable.yukihookapi:ksp-xposed:${rootProject.extra["YuKiHookVersion"]}")
//    // Kotlin 反射库
//    implementation("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra["kotlin.version"]}")
}

/*
kotlin{
    sourceSets.all {
        languageSettings.apply {
            languageVersion = "2.0"
        }
    }
}

 */




