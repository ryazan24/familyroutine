plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Раскомментируйте после добавления google-services.json:
    // id("com.google.gms.google-services")
}

android {
    namespace = "com.familyroutine"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.familyroutine"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.2"
    }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Firebase: синхронизация между устройствами родителя и ребёнка.
    // Синхронизация активируется автоматически, как только вы добавите google-services.json.
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-database-ktx")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
