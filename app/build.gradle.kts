import org.jetbrains.dokka.DokkaConfiguration.*
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // Added:
    alias(libs.plugins.de.undercouch.gradle.download)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.dokka)
}

android {
    namespace = "p4ulor.mediapipe"
    compileSdk = 34

    defaultConfig {
        applicationId = "p4ulor.mediapipe"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // shrinks code
            isShrinkResources = true // removes resources that ProGuard flagged as unused
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {  }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true // Added after the tests failing with: com.android.builder.errors.EvalIssueException: defaultConfig contains custom BuildConfig fields, but the feature is disabled
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Added:
    println("Building version: ${defaultConfig.versionName})")
}

dependencies {

    // Default:
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.tasks.vision)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Added:
    // Camera https://developer.android.com/jetpack/androidx/releases/camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2) // CameraX is built on top of camera2, so it's required
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions) // https://developer.android.com/media/camera/camera-extensions

    // MediaPipe - vision tasks
    // - https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector/android
    // - https://ai.google.dev/edge/api/mediapipe/java/com/google/mediapipe/tasks/vision/objectdetector/package-summary
    // - https://mvnrepository.com/artifact/com.google.mediapipe/tasks-vision
    implementation(libs.tasks.vision)

    // Permission utils
    implementation(libs.accompanist.permissions)
    // More MaterialIcons
    implementation(libs.androidx.material.icons.extended.android)

    // Ktor HTTP client, to make HTTP requests to the Gemini API
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio) // Coroutine-based I/O Engine for processing network requests https://ktor.io/docs/client-engines.html#jvm-android-native
    implementation(libs.ktor.client.content.negotiation) // https://ktor.io/docs/client-serialization.html
    implementation(libs.ktor.serialization.kotlinx.json) // ^. But still requires the Serialization Gradle Plugin

    // Mockk
    testImplementation(libs.mockk)

    // kotlin.test for utility methods to allow parameter naming, while JUnit does not
    testImplementation(kotlin("test"))
    testImplementation(libs.coroutines.test)

    // JUnit (5 AKA Jupiter) so I can use BeforeAllCallback and AfterAllCallback
    testRuntimeOnly(libs.jupiter.engine) // core
    testImplementation(libs.jupiter.api) // has the api.extensions
}

// Added:

// Necessary to run tests with JUnit Jupiter (JUnit 5), but also recognizes Kotlin test annotations
tasks.withType<Test> {
    useJUnitPlatform()
}

private val ASSET_DIR = "$projectDir/src/main/assets"

// The tasks have an underscore to appear at the top of the gradle task list in the gradle tab,
// at app/tasks/other
/**
 *  This model is recommended because it strikes a balance between latency and accuracy
 *  https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector#efficientdet-lite0_model_recommended
 */
task<Download>("_download1") {
    src("https://storage.googleapis.com/mediapipe-models/object_detector/efficientdet_lite0/float32/1/efficientdet_lite0.tflite")
    dest(File("$ASSET_DIR/efficientdet-lite0.tflite"))
    overwrite(false) // Prevents file from being downloaded again & overwritten
}

/**
 * This model is generally more accurate than EfficientDet-Lite0, but is also slower and more memory intensive
 * https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector#efficientdet-lite2_model
 */
task<Download>("_download2") {
    src("https://storage.googleapis.com/mediapipe-models/object_detector/efficientdet_lite2/float32/1/efficientdet_lite2.tflite")
    dest(File("$ASSET_DIR/efficientdet-lite2.tflite"))
    overwrite(false)
}

/** Download models after building */
tasks.named("build") {
    finalizedBy("_download1")
    finalizedBy("_download2")
}

/**
 * Enables context receivers
 * https://kotlinlang.org/docs/whatsnew1620.html#prototype-of-context-receivers-for-kotlin-jvm
 * https://kotlinlang.org/docs/whatsnew2020.html#phased-replacement-of-context-receivers-with-context-parameters
 */
tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOfNotNull("-Xcontext-receivers")
    }
}

/** https://kotlinlang.org/docs/dokka-gradle.html */
tasks.dokkaHtml {
    moduleName.set(rootProject.name)
    moduleVersion.set("${android.defaultConfig.versionName}")

    dokkaSourceSets.configureEach {
        perPackageOption {
            reportUndocumented.set(true)
            documentedVisibilities.set(Visibility.values().toSet())
        }
    }

    outputDirectory.set(project.rootDir.resolve("docs/dokka-generated"))
}

