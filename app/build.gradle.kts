import org.jetbrains.dokka.DokkaConfiguration.*
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // Added (read readme):
    alias(libs.plugins.de.undercouch.gradle.download)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
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
        // https://developer.android.com/build/shrink-code
        release {
            isDebuggable = false
            isMinifyEnabled = true // Shrinks and obfuscate code (but it's not enough to protect against decompilers of the .apk)
            isShrinkResources = true // removes unused resources, performed by Android Gradle plugin
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
        buildConfig = true // Enables the use of the generated BuildConfig.java, used in Logging.kt
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

    // Default (with some things removed):
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
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
    // implementation(libs.androidx.camera.extensions) // https://developer.android.com/media/camera/camera-extensions

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
    implementation(libs.ktor.client.content.negotiation) // Used for Serialization of JSONs https://ktor.io/docs/client-serialization.html
    implementation(libs.ktor.serialization.kotlinx.json) // Used for Serialization. The annotations are processed by the kotlinxSerialization Gradle Plugin

    // Koin
    // https://insert-koin.io/docs/setup/koin#android
    // https://insert-koin.io/docs/setup/annotations#android--ktor-app-ksp-setup
    implementation(libs.koin.android) // Core lib for Android
    implementation(libs.koin.androidx.compose) // https://insert-koin.io/docs/quickstart/android-compose/#injecting-viewmodel-in-compose
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler) // Indicate gradle to use Koin's KSP compiler with KSP to generate code, and use that code as a dependency

    // Datastore, to store app settings
    implementation(libs.androidx.datastore.preferences)

    // Lottie, for animated icons/images
    implementation(libs.lottie.compose)

    // kotlin.test for utility methods to allow parameter naming, while JUnit does not
    testImplementation(kotlin("test"))
    testImplementation(libs.coroutines.test)

    // JUnit (5 AKA Jupiter) so I can use BeforeAllCallback and AfterAllCallback
    testRuntimeOnly(libs.jupiter.engine) // core
    testImplementation(libs.jupiter.api) // has the api.extensions

    // Mockk
    testImplementation(libs.mockk)
}

// Added:

/**
 * Necessary to run tests with JUnit Jupiter (JUnit 5), while still recognizing Kotlin test
 * annotations. Make sure to also remove:
 * - testImplementation(libs.junit)
 * - androidTestImplementation(libs.androidx.junit)
 */
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

/**
 *  Configurs the dokkaHtml gradle task, to generate the documentation pages
 *  https://kotlinlang.org/docs/dokka-gradle.html
 */
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

// https://insert-koin.io/docs/reference/koin-annotations/start#compile-safety---check-your-koin-config-at-compile-time-since-130
ksp {
    arg("KOIN_CONFIG_CHECK","true")
}

/**
 * Extra, is interesting and might be used for something
 * ./gradlew testDebugUnitTest --tests p4ulor.mediapipe.unit.misc.MathTests
 */
tasks.withType<Test>().configureEach {
    var startTime: Long = 0

    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}

        override fun beforeTest(testDescriptor: TestDescriptor) {
            startTime = System.currentTimeMillis()
            println("Running test: ${testDescriptor.className}#${testDescriptor.name}")
        }

        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            println("Finished test: ${testDescriptor.className}/${testDescriptor.name} in $duration ms")
        }

        override fun afterSuite(suite: TestDescriptor, result: TestResult) {}
    })

    // alternative
    /*beforeTest(closureOf<TestDescriptor> {
        startTime = System.currentTimeMillis()
        println("Running ${this.className}#${this.name}")
    })*/

    doLast {
        println("Test finished")
    }
}