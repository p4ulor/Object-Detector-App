## Notes 📝
- The ML models used with MediaPipe need to be a compatible with it, the compatibility depends on the feature used
- MediaPipe uses/is based on the TensorFlow Lite, and provides an easier way to add some basic ML capabilities to your apps. Checkout the [ML on Android with MediaPipe](https://www.youtube.com/playlist?list=PLOU2XLYxmsILZnKn6Erxdyhxmc3fxyitP) YT playlist.
- Apps that use MediaPipe will generally not run in phone emulators, you will need a physical Android device to run this app
- MediaPipe's runs the model on your phone's CPU or GPU (I set GPU by default).
- If the object detection outline isn't smooth, it's because you have toggled on the "Reduce Animations" in your Android's settings

## Technical documentation 📜
This text details things about conditions, state handling, decisions made, reminders, errors, user experience, etc. The actual details are in the code of course.

### HomeScreen 🏠
- Everytime the user navigates to this screen the preferences are loaded
- When existing the screen or minimizing the app, the camera is unbinded
- Toggling on Gemini doesn't stop the image analysis from MediaPipe from running in the background because I didn't want to have to rebind the camera just to do so, but it stops the flow emissions of results
- For simplicity sake, only the latest message of Gemini is saved when the user leaves the screen or toggled off/on Gemini

### SettingsScreen 🔧
- Everytime the user navigates to this screen the preferences are loaded
- When the app that's installed doesn't have the MediaPipe models: The preferences are still step, but MediaPipe won't be running in the ImageAnalysis use case

### AchievementsScreen 🏅


## Authentication 🛂
- [App signing](https://developer.android.com/studio/publish/app-signing#generate-key) is used to authenticate the clients (android phones) doing requests to the Firebase project. So the SHA-1 that's used in production should be the one that's printed form the "release" signingConfig, which is shown when running `./gradlew signingReport`. 
- When setting up the "Add Firebase to your Android App" it asks for the "Debug signing certificate SHA-1", which I provide, so this should be changed when releases are provided or we need to see if we have another signature for releases so we can support both debug and release app instalations performing Firebase requests

The Java KeyStore file is converted to Build64 so it's easily set in the pipeline and `build.gradle.kts` converts it back to a temp JKS file.
```
base64 --wrap=0 app_certificate.jks > app_certificate.base64
```
## Source Code Structure

```mermaid
graph TD
%% Root packages and files
    Koin("<img style="max-height: 20px; object-fit: contain" src="https://insert-koin.io/img/koin_new_logo.png"> DependencyInjection </>")
    Log("<img style="max-height: 20px; object-fit: contain" src="./imgs/logs.png"> Logging </>")
    A(android)
    D(data)
    U(ui)

%% Sub packages
    A --> B1(activities)
    A --> B2(utils)
    A --> B3(viewmodels)
    A --> B4(MyApplication)

    B1 --> B11(utils)
    B2 --> B21(camera)
    B3 --> B31(utils)

    D --> D1(domains)
    D --> D2(sources)
    D --> D3(storage)
    D --> D4(utils)

    D1 --> D11(firebase)
    D1 --> D12(gemini)
    D1 --> D13(mediapipe)

    D2 --> D21(gemini)
    D2 --> D22(utils)

    D3 --> D31(preferences)

    U --> U1(animations)
    U --> U2(components)
    U --> U3(screens)
    U --> U4(theme)

    U2 --> U21(chat)
    U2 --> U22(utils)

    U3 --> U31(achievements)
    U3 --> U32(home)
    U3 --> U33(root)
    U3 --> U34(settings)

    U32 --> D321(outline)
%% Styles
    classDef screenStyle color:#FFFFFF, stroke:#00C853
    classDef noBackgroundStyle color:#FFFFFF, fill:#0d1117

    class HS,SC,AS screenStyle
    class DB,FB,PD,MP,GEM,KTOR,GeminiApiService noBackgroundStyle
```

## Main challenges
- Custom and made from the ground up floating action button and it's bounds and state handling
- GeminiChat feature, state management and persistence (could be improved)

## Things that were not done for the sake of moving on to other things
- Animated detection outlines only support detection 1 type of object, since an identifier is required for the animation to track, but MediaPipe doesn't provide identifiers.
- Not worrying about still using ImageDetectionUseCase when toggling Gemini mode
- Displaying only the latest Gemini message when toggling off and on Gemini, instead of storing the list of messages
- The notification intent explicitly opening the Achivements screen instead of just pointing to the MainActivity

### Resources
- https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/object_detection/android
- https://firebase.google.com/docs/samples?hl=en&authuser=0

### Trivia
- In the past before my decision to use Gemini, I intended to use [Text Generation](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android)
    - But it would require the user to download a LLM to their phone. So I decided to go for the Gemini API.
    - The LLM Inference API support these [types of models](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference#models). The most lightwheight (1.30 GB) is:
[gemma-1.1-2b-it-cpu-int4, LiteRT/TFLite variation](https://www.kaggle.com/models/google/gemma/tfLite/gemma-1.1-2b-it-cpu-int4)
    - [mediapipe-samples - llm_inference](https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/llm_inference/android).

- Update, with Gemma 3, the model is 500MB which is much better
    - https://www.linkedin.com/posts/aleks-denisov_ai-edgeai-gemma-activity-7307364307208458240-tLBb

> [!NOTE]
> Use the following VSC extensions for better experience in markdown docs
> - https://marketplace.visualstudio.com/items?itemName=bierner.markdown-mermaid
> - https://marketplace.visualstudio.com/items?itemName=bierner.markdown-preview-github-styles

