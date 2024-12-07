plugins {
    alias(libs.plugins.android.application)
    // Jedidi Add
    //id("com.android.application")
    // Jedidi Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    // Jedidi Add
    viewBinding { enable = true }
    namespace = "tn.isims.fantasy"
    compileSdk = 34

    defaultConfig {
        applicationId = "tn.isims.fantasy"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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


}

dependencies {

    // Jedidi Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))


    // Jedidi TODO: Add the dependencies for Firebase products you want to use
    // Example: Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.android.gms:play-services-auth")
    // Example: Firebase Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")
    // Jedidi When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // Jedidi Add the dependency for the Realtime Database library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-database")
    // Jedidi add Firebase Storage
    implementation("com.google.firebase:firebase-storage")

    // Jedidi Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}