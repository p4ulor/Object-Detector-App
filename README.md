<div align=center>
    <img width=100 src="./app/src/main/res/mipmap-xxxhdpi/app_icon_foreground.webp"/>
    <p>Object Detector</p>
</div>

## Demo 🎥

## Main Features ✨
- Detect very simple objects with MediaPipe
- Ask Gemini anything about a picture taken
- Tracks the user's achievements towards MediaPipe's detectable objects and provides a leaderboard for the users (that opted in by logging in with Google) towards those achievements. Along with statistics regarding user-wide detections.
- It supports various customizations and other features: 
    - Camera settings: ratio, flashlight and saving pictures taken
    - Model settings: detection sensitivity, max detection count, detection animations and the model itself (2 choices)
    - Notifications for achievements

## Primary Technologies 🛠️
| MediaPipe | Gemini API | Ktor | Firebase |
|:-:|:-:|:-:|:-:|
| <img width="50" src='https://ai.google.dev/edge/mediapipe/images/mediapipe_icon.svg'> | <img width="50" src='https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/google-gemini-icon.png'> | <img width="50" src='https://resources.jetbrains.com/storage/products/company/brand/logos/Ktor_icon.png'> | <img width="50" src='https://firebase.google.com/static/images/brand-guidelines/logo-logomark.png'> |

### 1. [MediaPipe](https://github.com/google/mediapipe)
- An open-source framework that facilitates the integration of AI & ML, made by Google. It's mostly geared towards lightweight devices or "edge devices" (mobile devices and embedded systems) and is cross-platform. 
- MP supports models authored in [LiteRT](https://developers.googleblog.com/en/tensorflow-lite-is-now-litert/) (formelly known as TensorFlow Lite (TFLite)), which is a framework and runtime for on-device AI that's also built by Google. LiteRT also supports models authored in PyTorch by [converting them](https://ai.google.dev/edge/litert/models/convert_pytorch)
- Here's a showcase of the [tasks MediaPipe supports](https://mediapipe-studio.webapps.google.com/home)
- I'm using the [Object Detection](https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector/android) Mediapipe dependency and the provided pre-trained [models](https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector#models) of the family "EfficientDet" developed by Google. These models can detect these [80 objects](https://storage.googleapis.com/mediapipe-tasks/object_detector/labelmap.txt)
- You can learn more about object detection (in this example with PyTorch) and in making your own model through these [DeepLearningExamples repository](https://github.com/NVIDIA/DeepLearningExamples/blob/master/PyTorch/Detection/README.md) by NVIDIA

### 2. [Gemini API](https://aistudio.google.com/app/apikey)
- A RESTful API for performing HTTP requests to a Gemini model,
running in Google Cloud Platform. Doesn't require a GCP project, although you can associate it to
one or monitor the use of your API key
- Pricing: It has a reasonable free tier of [1.5k requests per day and other limits](hhttps://ai.google.dev/gemini-api/docs/rate-limits#current-rate-limits) for Gemini 2.0 Flash. [See it's capabilities and various details](https://ai.google.dev/gemini-api/docs/models/gemini#gemini-1.5-flash)

### 3. [Ktor (client)](https://ktor.io/docs/client-create-new-application.html)
- An open-source framework developed by JetBrains to create HTTP servers or clients
- I only use it's client component, in order to make HTTP calls to the Gemini API
- I use the built-in JSON serializer for sending and receiving objects

### 4. [Firebase](https://firebase.google.com/docs/build)
- A cloud platform or BaaS (Backend As A Service) that provides a wide range of utilities for different environments (and programming languages) via SDK's (Software Development Kits) for each one. Firebase runs on Google Cloud infrastructure, but it contains Firebase-specific configurations and services
- I use it to store the achievement leaderboard of the users using [Cloud Firestore](https://firebase.google.com/docs/database/rtdb-vs-firestore?hl=en&authuser=0), which is a NoSQL document database. And also Firebase [Authentication](https://firebase.google.com/docs/auth) is used in order to sign-in users with Google
- [Cloud Functions](https://firebase.google.com/products/functions/?hl=en&authuser=0) are used to make use of [Firestore triggers](https://firebase.google.com/docs/functions/firestore-events?hl=en&authuser=0&gen=2nd) to update the leaderboard and stats on document changes, and it requires setting up a Pay as you go (Blaze) billing plan
- [Check pricing here](https://firebase.google.com/pricing). The pay-start ceiling for Cloud functions is very high for the use case of the app, so it's basically free. You can also use the [300$ trial](https://firebase.blog/posts/2024/11/claim-300-to-get-started)

## Secondary Technologies 🛠️
| [Koin](https://insert-koin.io/docs/quickstart/android-annotations/) |       [Lottie](https://airbnb.io/lottie/#/android-compose)       |                            [Mockk](https://mockk.io/)                             |                      [Coil](https://coil-kt.github.io/coil/)                      |                             [Jetpack Compose UI Test](https://developer.android.com/develop/ui/compose/testing)                              | 
|:-------------------------------------------------------------------:|:----------------------------------------------------------------:|:---------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------:|
| <img width="50" src='https://insert-koin.io/img/koin_new_logo.png'> | <img width="50" src='https://airbnb.io/lottie/images/logo.webp'> | <img width="50" src='https://avatars.githubusercontent.com/u/34787540?s=200&v=4'> | <img width="50" src='https://avatars.githubusercontent.com/u/52722434?s=200&v=4'> | <img width="50" src='https://raw.githubusercontent.com/devicons/devicon/refs/heads/master/icons/jetpackcompose/jetpackcompose-original.svg'> |
|                      For dependency injection                       |                      For animated graphics                       |                            For object mocking in tests                            |                      For efficient & flexible image loading                       |                                                             For (some) UI tests                                                              |

### Plugins Used & Other Dependencies 🔌
#### Gradle Plugins 🐘
- [gradle-download-task by Michel Krämer](https://github.com/michel-kraemer/gradle-download-task) -> To facilitate the download of the small TFlite ML models. It is ran automatically after building the project. In gradle tool window, access it in the task category "other"
- [Kotlin Serialization](https://kotlinlang.org/docs/serialization.html) -> Used to process Kotlin's @Serialization annotations
- [Kotlin Symbol Processing (KSP)](https://kotlinlang.org/docs/ksp-quickstart.html#add-a-processor) -> Used to process Koin's and Room's annotations and build the dependencies
- [Dokka](https://kotlinlang.org/docs/dokka-introduction.html) -> Documentation engine for KDoc comments. Run it via `./gradlew app:dokkaHtml`. Or in gradle tool window, access it in the task category "documentation"
- [Google Services](https://firebase.google.com/docs/android/setup#add-config-file) -> Used to process the `google-services.json` file and build other resources for the Firebase SDK to work

#### Other Dependencies
- [androidx.camera.* dependencies](https://developer.android.com/jetpack/androidx/releases/camera)
- [google-accompanist](https://google.github.io/accompanist/) -> For permission utils
- [androix.datastore](https://developer.android.com/jetpack/androidx/releases/datastore) -> For the storage of user preferences
- [androidx.room](https://developer.android.com/jetpack/androidx/releases/room) -> For storage of more complex data-structures, like the achievements

## Core Tech Use Throughout The App (simplified)

![](./docs/imgs/mermaid-digram_tech-use.png)

<details closed>
<summary>More details</summary>

- [docs](./docs)
![](./docs/imgs/diagram.png)
- [GH rendered Mermaid diagrams](./docs/mermaid-diagrams.md)
</details>

## Setup Guide 🙌
[Gradle JDK](https://www.jetbrains.com/help/idea/gradle-jvm-selection.html#jvm_settings) used: JetBrains Runtime (JBR) 17.0.10

1. Get API Keys 🔑
    - [Gemini API](https://aistudio.google.com/app/apikey). There's a link to this in the app too so you can better copy paste it from your phone's browser
    - 
2. Installing ⬇️
    - Minimum Android version: 11 (API level 30)
    - a) From .apk file: Download in [releases](https://github.com/p4ulor/Object-Detector-App/releases). Built with Github Actions
    - b) From source code: 
        - Decide if you want to comment out all firebase code so you don't create a firebase project or create your own and add your `google-services.json` file. Then try building the project
        - Connect your phone to the PC and run in a terminal at the root directory `./gradlew app:installDebug`
        - Or `./gradlew assembleDebug` to build a debug version, which will output an .apk file to `app/build/outputs/apk/debug`
3. Clear your data from the Firebase project 🗑
    - When you're done with the project/app, you can clear your data like so:
    1. Manually delete your data (including your achievements),stored in Firebase's Firestore, through the app 
    2. Go to `My Google Account` -> `Security` -> [Your connection to third-party apps & services](https://myaccount.google.com/connections)
    3. And look for `Object Detector`. This will clear you account entry in Firebase's Authentication

## Todo 
- Add sfx
- Shrink and obfuscate apk https://developer.android.com/build/shrink-code#obfuscate using proguard. check results with https://github.com/Konloch/bytecode-viewer
- CI & CD:
  - https://docs.github.com/en/actions/using-github-hosted-runners/using-github-hosted-runners/about-github-hosted-runners
  - add docs github page https://github.com/p4ulor/Object-Detector-App/settings/pages
  - https://github.com/marketplace/actions/github-pages-overwriter
  - https://github.com/rayluo/github-pages-overwriter/blob/dev/action.yml
- https://gist.github.com/cobyism/4730490#gistcomment-1374989
  - https://github.com/skydoves/Balloon/blob/main/.github/workflows/release-docs.yaml
### Fixes To-do 🕒

- Not using the deprecated resolution selector causes
```
java.lang.RuntimeException: Buffer not large enough for pixels" at bitmapBuffer.copyPixelsFromBuffer
```
