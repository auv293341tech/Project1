plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.project1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.project1"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {

    // Android core
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.gridlayout:gridlayout:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")
    implementation("androidx.preference:preference-ktx:1.2.1")

    // 🔥 Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // 🔐 Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.5.0")

    // 🗄 Firebase Firestore
    implementation("com.google.firebase:firebase-firestore")

    // ☁️ Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging")

    // 🖼️ Firebase Storage
    implementation("com.google.firebase:firebase-storage")

    // 🗺 FREE MAP (OpenStreetMap - NO BILLING)
    implementation("org.osmdroid:osmdroid-android:6.1.16")
    implementation("androidx.activity:activity:1.8.2")

    // 🗺 Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // 🖼️ Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Circle ImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")
    
    // Material Components
    implementation("com.google.android.material:material:1.11.0")

}
