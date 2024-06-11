plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)

    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
    id("kotlin-parcelize")
}

android {
    namespace = "com.org.capstone.nutrifish"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.org.capstone.nutrifish"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            val baseUrl = "https://test-express-js-zk5psstbbq-et.a.run.app"
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

            val webClientId = "297202612550-hi8p73ot6spt0felvv87rgokllf2r3e1.apps.googleusercontent.com"
            buildConfigField ("String", "WEB_CLIENT_ID", "\"${webClientId}\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            val baseUrl = "https://test-express-js-zk5psstbbq-et.a.run.app"
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

            val webClientId = "297202612550-hi8p73ot6spt0felvv87rgokllf2r3e1.apps.googleusercontent.com"
            buildConfigField ("String", "WEB_CLIENT_ID", "\"${webClientId}\"")


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
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.fragment.ktx)

    //circle image view
    implementation(libs.circleimageview)

    //firebase
    implementation(libs.firebase.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore.ktx)

    // Credential Manager
    implementation(libs.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.credentials)

    //Camera
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.exifinterface)

    //Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    //Glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //tensorflow lite
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite.task.vision)

    //Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)



}