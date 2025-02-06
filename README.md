## Demo 🎥

## Main features ✨
- Detect very simple objects
- Ask Gemini to talk about the object or describe a picture taken (TODO)
- Save and manage results (TODO)

### Side features
- Change camera settings: Ratio and flashlight
- Change model settings: sensitivity, max detection count, etc  (TODO)
- Achievements list for the 80 Media Pipe objects (TODO)
- Notification on new object detected as part of an achievement (TODO)

## Primary Technologies 🛠️
| MediaPipe | Gemini API | Ktor | Firebase |
|:-:|:-:|:-:|:-:|
| <img width="50" src='https://ai.google.dev/edge/mediapipe/images/mediapipe_icon.svg'> | <img width="50" src='https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/google-gemini-icon.png'> | <img width="50" src='https://resources.jetbrains.com/storage/products/company/brand/logos/Ktor_icon.png'> | <img width="50" src='https://firebase.google.com/static/images/brand-guidelines/logo-logomark.png'> |

### 1. [MediaPipe](https://github.com/google/mediapipe)
- An open-source project from Google. It's a framework that facilitates the integration of AI & ML into your applications. Here is a showcase of the [tasks MediaPipe supports](https://mediapipe-studio.webapps.google.com/home)
- I'm using [Object Detection](https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector/android)
    - I used the provided pre-trained [models](https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector#models), which can detect these [80 objects](https://storage.googleapis.com/mediapipe-tasks/object_detector/labelmap.txt)
    - I used [mediapipe-samples - object_detection](https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/object_detection/android-jetpack-compose) as a reference (warning: insane spaghetti code...)
- I intended to use [Text Generation](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android)
    - But it would require the user to download a LLM to their phone. So I decided to go for the Gemini API, and maybe add this feature later, to make it possible to make use of the advantage of offline AI use and just to see how it can be implemented
    - The LLM Inference API support these [types of models](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference#models). The most lightwheight (1.30 GB) is:
[gemma-1.1-2b-it-cpu-int4, LiteRT/TFLite variation](https://www.kaggle.com/models/google/gemma/tfLite/gemma-1.1-2b-it-cpu-int4)
    - [mediapipe-samples - llm_inference](https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/llm_inference/android).

### 2. [Gemini API](https://aistudio.google.com/app/apikey)
- A RESTful API that allows you to perform HTTP requests to a Gemini model,
running in Google Cloud Platform. Doesn't require a GCP project, although you can associate it to
one and monitor the use of your API key
- Pricing: It has a reasonable free tier of [1.5k requests per day and other limits](https://ai.google.dev/gemini-api/docs/billing#about-billing) for Gemini 1.5 Flash. [See it's capabilities and various details](https://ai.google.dev/gemini-api/docs/models/gemini#gemini-1.5-flash)

### 3. [Ktor (client)](https://ktor.io/docs/client-create-new-application.html)
- An open-source framework developed by JetBrains to create HTTP servers or clients
- I only use it's client component, in order to make HTTP calls to the Gemini API
- I use the built-in JSON serializer for sending and receiving objects

### 4. [Firebase](https://firebase.google.com/docs/build)
- A cloud platform or BaaS (Backend As A Service) that provides a range of utilities for different environments (and programming languages) via SDK's (Software Development Kits)
- I use it to store the results of prompts from the Gemini API
- Pricing: [Free tier](https://firebase.google.com/pricing)

## Secondary Technologies 🛠️
| Koin | Lottie | Mockk | Coil | [Jetpack Compose UI Test](https://developer.android.com/develop/ui/compose/testing) | 
|:----:|:------:|:-----:|:-----:|:-----:|
| <img width="50" src='https://insert-koin.io/img/koin_new_logo.png'> | <img width="50" src='https://airbnb.io/lottie/images/logo.webp'> | <img width="50" src='https://avatars.githubusercontent.com/u/34787540?s=200&v=4'> | <img width="50" src='https://avatars.githubusercontent.com/u/52722434?s=200&v=4'> | <img width="50" src='https://raw.githubusercontent.com/devicons/devicon/refs/heads/master/icons/jetpackcompose/jetpackcompose-original.svg'> |
| For dependency injection | For animated graphics | For object mocking in tests | For efficient image loading  | For UI tests

### Other Dependencies & Plugins Used 🔌
#### Gradle Plugins 🐘
- [gradle-download-task by Michel Krämer](https://github.com/michel-kraemer/gradle-download-task) -> To facilitate the download of the small TFlite ML models. It is ran automatically after building the project. In gradle tool window, access it in the task category "other"
- [Kotlin Serialization](https://kotlinlang.org/docs/serialization.html) -> Used to process Kotlin's @Serialization annonations
- [Kotlin Symbol Processing (KSP)](https://kotlinlang.org/docs/ksp-quickstart.html#add-a-processor) -> Used to process Koin's annonations and build the dependencies
- [Dokka](https://kotlinlang.org/docs/dokka-introduction.html) -> API documentation engine for Kotlin. Run it via `./gradlew app:dokkaHtml`. Or in gradle tool window, access it in the task category "documentation"
- [secrets-gradle-plugin by Google](https://github.com/google/secrets-gradle-plugin) -> to think
#### Other
- [androidx.camera.*dependencies](https://developer.android.com/jetpack/androidx/releases/camera)
- [google-accompanist](https://google.github.io/accompanist/) -> For permission utils

## Getting Started / Setup Guide 🙌
- [Gradle JDK](https://www.jetbrains.com/help/idea/gradle-jvm-selection.html#jvm_settings) used: JetBrains Runtime (JBR) 17.0.10

### Getting API Keys 🔑
- Gemini API -> https://aistudio.google.com/app/apikey

### Installing ⬇️
- a) From .apk file: Download in releases
- b) With source code: Connect your phone to the PC and run in a terminal at the root directory `./gradlew app:installDebug`

## Notes 📝
- The Machine Learning models used with MediaPipe need to be a compatible with it, the compatability depends on the feature used
- MediaPipe uses/is based on the TensorFlow Lite, and provides an easier way to add some basic ML capabilities to your apps. Checkout the [ML on Android with MediaPipe](https://www.youtube.com/playlist?list=PLOU2XLYxmsILZnKn6Erxdyhxmc3fxyitP) YT playlist.
- Apps that use MediaPipe will generally not run in phone emulators, you will need a physical Android device to run this app
- MediaPipe's runs the model on your phone's CPU or GPU.
- I'm using: Jetpack Compose, Gradle's Kotlin DSL, [Gradle version catalogs](https://developer.android.com/build/migrate-to-catalogs)
- If the object detection overlay isn't smooth, it's because you have toggled on the "Reduce Animations" in your Android's settings

## Technical notes & Project structure

> [!NOTE]  
> License: © 2024-currentYear, Paulo Rosa, [all reserved to me](https://choosealicense.com/no-permission/). Do not distribute to any public the entirety or individual parts (files and text) of the project without my consent. Do not claim you're the original author. Downloading / git cloning is inevitably allowed, in order to only build the artifacts and modify the project locally and privately for non-commercial use. Pull requests are welcomed. If a fork was done do not modify this current file, specially this license.

## Todo 🕒
- Add some mermaid diagrams
- Add sfx
- Review and clean up the architecture
- Shrink and obfuscate apk https://developer.android.com/build/shrink-code#obfuscate using proguard. check results with https://github.com/Konloch/bytecode-viewer
- Listen to networks changes to display gemini as available or not
- fix The application may be doing too much work on its main thread.
- Make more composables previewable
- add docs github page https://github.com/p4ulor/Object-Detector-App/settings/pages
### Fixes
- Fix not using deprecated resolution selector causing
```
java.lang.RuntimeException: Buffer not large enough for pixels" at bitmapBuffer.copyPixelsFromBuffer
```
