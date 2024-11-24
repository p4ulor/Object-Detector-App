## Technology introduction
- [MediaPipe](https://github.com/google/mediapipe) is an open-source project from Google. It's a framework that facilitates the integration of AI & ML into your applications. Here is a showcase of the [tasks MediaPipe supports](https://mediapipe-studio.webapps.google.com/home)

### [Object Detection](https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector/android)
- I used the provided pre-trained [models](https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector#models), which can detect these [80 objects](https://storage.googleapis.com/mediapipe-tasks/object_detector/labelmap.txt)
- [mediapipe-samples - object_detection](https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/object_detection/android-jetpack-compose) (insane spaghetti code...)

### [Text Generation](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android)
- The LLM Inference API support these [types of models](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference#models). I decided to use the most lightwheight (1.30 GB) I could find:
[gemma-1.1-2b-it-cpu-int4, LiteRT/TFLite variation](https://www.kaggle.com/models/google/gemma/tfLite/gemma-1.1-2b-it-cpu-int4)
- [mediapipe-samples - llm_inference](https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/llm_inference/android)
### Notes
- The models need to have a compatible format with MediaPipe
- MediaPipe uses/is based on the TensorFlow Lite, and provides an easier way to add some basic ML capabilities to your apps. Checkout the [ML on Android with MediaPipe](https://www.youtube.com/playlist?list=PLOU2XLYxmsILZnKn6Erxdyhxmc3fxyitP) YT playlist.

## Demo


## App features
- Detect very simple objects
- Describe the object
- Change model settings

## Details
This app doesn't require API keys or Internet. The app runs the model on your phone's CPU or GPU.
I'm using:
- Jetpack Compose
- Gradle's Kotlin DSL
- [Gradle version catalogs](https://developer.android.com/build/migrate-to-catalogs)

## Dependencies
- Gradle Plugin [gradle-download-task by Michel Krämer](https://github.com/michel-kraemer/gradle-download-task) to facilitate the download of the small TFlite AI/ML models
- [androidx.camera.* dependencies](https://developer.android.com/jetpack/androidx/releases/camera)
- MediaPipe Tasks Vision 
	- https://mvnrepository.com/artifact/com.google.mediapipe/tasks-vision
	- https://ai.google.dev/edge/api/mediapipe/js/tasks-vision
- [com.google.accompanist](https://google.github.io/accompanist/) for permission utils

## Todo
- Fix log spam when exiting app
```
updateSurface: surface is not valid
SurfaceView@2f2a982 p4ulor.mediapipe I releaseSurfaces: viewRoot = ViewRootImpl@f434e9e[MainActivity]
```
- Fix not using deprecated resolution selector causing
```
java.lang.RuntimeException: Buffer not large enough for pixels" at bitmapBuffer.copyPixelsFromBuffer
```
