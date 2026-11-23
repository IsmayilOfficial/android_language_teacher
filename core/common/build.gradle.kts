plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.rusdili.pro.core.common"
    compileSdk = 34
    defaultConfig { minSdk = 24 }
}

dependencies {
    implementation(libs.datastore.preferences)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
