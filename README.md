## Technology introduction
- [MediaPipe](https://github.com/google/mediapipe) is an open-source project from Google. It's a framework that facilitates the integration of AI & ML into your applications. It provides pre-trained models like it's [shown here](https://mediapipe-studio.webapps.google.com/home). The [models](https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector#models) can detect these [80 objects](https://storage.googleapis.com/mediapipe-tasks/object_detector/labelmap.txt)
- Note: MediaPipe uses or is based on the TensorFlow Lite, and provides an easier way to add some basic ML capabilities to your apps. Checkout the [ML on Android with MediaPipe](https://www.youtube.com/playlist?list=PLOU2XLYxmsILZnKn6Erxdyhxmc3fxyitP) YT playlist.
- I was inspired by [mediapipe-samples - object_detection](https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/object_detection/android-jetpack-compose). Which has a very spaghetti and hard to read code, but it helped nonetheless

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

