import java.util.Properties
import java.io.FileInputStream

// [1. local.properties 읽기 로직 추가]
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.najunho.rememberbooks"
    compileSdk = 36

    defaultConfig {
        // [2. BuildConfig 필드 추가]
        // 코드에서 BuildConfig.KAKAO_KEY 등으로 접근 가능합니다.
        buildConfigField("String", "KAKAO_KEY", "\"${localProperties.getProperty("kakao_native_app_key")}\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${localProperties.getProperty("google_web_client_id")}\"")
        buildConfigField("String", "ALADDIN_KEY", "\"${localProperties.getProperty("aladdin_ttb_key")}\"")

        // [3. Manifest용 변수 추가]
        // AndroidManifest.xml에서 ${kakaoNativeAppKey}로 접근 가능합니다.
        manifestPlaceholders["kakaoNativeAppKey"] = localProperties.getProperty("kakao_native_app_key") ?: ""

        applicationId = "com.najunho.rememberbooks"
        minSdk = 28
        targetSdk = 36
        versionCode = 7
        versionName = "1.0.7"

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
    // [수정된 코드 - 추가하세요]
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        // [4. BuildConfig 기능 활성화] (중요: Kotlin DSL에서는 이 설정이 필수입니다)
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.cardview)
    implementation(libs.material)
    implementation(libs.firebase.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("com.google.android.material:material:1.13.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glide 라이브러리
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    // Glide 어노테이션 프로세서 (선택 사항이지만 권장)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-functions")
    implementation ("com.google.firebase:firebase-appcheck-playintegrity")
    implementation ("com.google.firebase:firebase-appcheck-debug") // 개발용

    // Google 로그인을 위한 Play Services 라이브러리 (Firebase Auth와 별개로 필요)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // 구글 광고 SDK
    implementation("com.google.android.gms:play-services-ads:24.9.0")

    implementation("com.kakao.sdk:v2-all:2.19.0")
    // 전체 모듈 추가, 2.11.0 버전부터 지원

    // ML Kit 한국어 텍스트 인식 라이브러리
    implementation("com.google.mlkit:text-recognition-korean:16.0.1")

    //ai sdk
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    // 비동기 처리를 위한 Guava (Java 환경 필수)
    implementation("com.google.guava:guava:31.1-android")

    //Markwon
    implementation("io.noties.markwon:core:4.6.2")

    // 캘린더 라이브러리
    implementation("com.applandeo:material-calendar-view:1.9.0-rc03")

}