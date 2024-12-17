## Demo 🎥

## App features ✨
- Detect very simple objects
- Change model settings
- Ask Gemini to talk about the object or describe a picture taken
- Save and manage results

## Main Technology introduction 🛠️
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
running in Google Cloud Platform
- Pricing: It has a reasonable free tier of [1.5k requests per day and other limits](https://ai.google.dev/gemini-api/docs/billing#about-billing) for Gemini 1.5 Flash. [See it's capabilities and various details](https://ai.google.dev/gemini-api/docs/models/gemini#gemini-1.5-flash)

### 3. [Ktor (client)](https://ktor.io/docs/client-create-new-application.html)
- An open-source framework developed by JetBrains to develop HTTP servers or clients
- I only use it's client components, in order to make HTTP calls to the Gemini API

### 4. [Firebase](https://firebase.google.com/docs/build)
- A cloud platform or BaaS (Backend As A Service) that provides a range of utilities for diferent enviornments (and programming languages) via SDK's (Software Development Kits)
- I use it to store the results of prompts from the Gemini API
- Pricing: [Free tier](https://firebase.google.com/pricing)

## Notes 📝
- The models used with MediaPipe need to be a compatible with it, the compatability depends on the feature used
- MediaPipe uses/is based on the TensorFlow Lite, and provides an easier way to add some basic ML capabilities to your apps. Checkout the [ML on Android with MediaPipe](https://www.youtube.com/playlist?list=PLOU2XLYxmsILZnKn6Erxdyhxmc3fxyitP) YT playlist.
- Apps that use MediaPipe will generally not run in phone emulators, you will need a physical Android device to run this app
- MediaPipe's runs the model on your phone's CPU or GPU.
- I'm using: Jetpack Compose, Gradle's Kotlin DSL, [Gradle version catalogs](https://developer.android.com/build/migrate-to-catalogs)

### Getting API Keys 🔑
- Gemini API -> https://aistudio.google.com/app/apikey
- Firebase -> 

### Other dependencies used 🔌
- Gradle Plugin [gradle-download-task by Michel Krämer](https://github.com/michel-kraemer/gradle-download-task) -> To facilitate the download of the small TFlite AI/ML models
- Gradle Plugin [secrets-gradle-plugin by Google](https://github.com/google/secrets-gradle-plugin)
- [androidx.camera.* dependencies](https://developer.android.com/jetpack/androidx/releases/camera)
- MediaPipe Tasks Vision 
	- https://mvnrepository.com/artifact/com.google.mediapipe/tasks-vision
	- https://ai.google.dev/edge/api/mediapipe/js/tasks-vision
- [com.google.accompanist](https://google.github.io/accompanist/) -> For permission utils

## Todo 🕒
- Fix log spam when exiting app
```
updateSurface: surface is not valid
SurfaceView@2f2a982 p4ulor.mediapipe I releaseSurfaces: viewRoot = ViewRootImpl@f434e9e[MainActivity]
```
- Fix not using deprecated resolution selector causing
```
java.lang.RuntimeException: Buffer not large enough for pixels" at bitmapBuffer.copyPixelsFromBuffer
```
