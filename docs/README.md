## Notes 📝
- The ML models used with MediaPipe need to be a compatible with it, the compatibility depends on the feature used. In the case of this app, it's a model made for Image Detection
- MediaPipe uses/is based on the TensorFlow Lite, and provides an easier way to add some basic ML capabilities to your apps. Checkout the [ML on Android with MediaPipe](https://www.youtube.com/playlist?list=PLOU2XLYxmsILZnKn6Erxdyhxmc3fxyitP) YT playlist.
- Apps that use MediaPipe will generally not run in phone emulators, you will need a physical Android device to run this app
- MediaPipe's runs the model on your phone's CPU or GPU (I set GPU by default).
- If the object detection outline isn't smooth, it's because you have toggled on the "Reduce Animations" in your Android's settings

## Technical documentation 📜
This text details things about conditions, state handling, decisions made, reminders, errors, user experience, etc. The actual details are in the code of course.

### HomeScreen 🏠
- Everytime the user navigates to this screen the preferences, secret preferences (gemini api key) and the unreached achievements are loaded
- When existing the screen or minimizing the app, the camera is unbinded
- Toggling on Gemini doesn't stop the image analysis from MediaPipe from running in the background because I didn't want to have to rebind the camera just to do so, but it stops the flow emissions of results
- For simplicity sake, only the latest message of Gemini is saved when the user leaves the screen or toggled off/on Gemini

### SettingsScreen 🔧
- Everytime the user navigates to this screen the preferences and secret preferences are loaded
- When the app that's installed doesn't have the MediaPipe models: The preferences are still set, but MediaPipe won't be running in the ImageAnalysis use case

### AchievementsScreen 🏅
- Everytime the user navigates to this screen, the list of achievements are loaded

## Firebase
1.
- `google-services.json` should be placed in `app/`

## Firebase & Authentication 🛂
## Setup
For properly setting up the Firebase project with authentication, a set of things must be done regarding the app signing and providing the SHA codes to the Firebase SDK setup for Android (the google-services.json). This will be used to authenticate the clients (android phones) doing requests to the Firebase project. You can change these SHA codes anytime, so you can leave it blank when creating the project's SDK.
1. Set up the [App signing](https://developer.android.com/studio/publish/app-signing#generate-key). This will be used when generating the SHA certificate fingerprints
2. Save the values and place them in `local.properties` like so
```cmake
# Should be also set in Github Actions
RELEASE_JKS_FILE_BASE64= ... (see step 3)
RELEASE_JSK_PASSWORD=...
RELEASE_KEY_ALIAS=...
RELEASE_KEY_PASSWORD=...
```
3. The previous step should create an `app_certificate.jks` (Java KeyStore (JKS)) file. Then run `base64 --wrap=0 app_certificate.jks > app_certificate.base64` and copy it's contents to the `RELEASE_JKS_FILE_BASE64` variable. The JKS file is converted to a Base64 string so it's easily set as a string environment variable in the Github Actions pipeline. The `build.gradle.kts` converts it back to a temp JKS file in order to work.
4. The previous operations and variable names should match the code inside the `signingConfigs` block in build.gradle.kts (:app) which setups up a release signing.
5. With the `app_certificate.base64` and the `signingConfigs` setup, the SHA-1 and SHA-256 can now be generated when running `./gradlew signingReport` (in Gradle task tree `app/Tasks/android`). It will print 2 pairs of these hashes: one for debug and one for release. So when setting up a release, a `google-services.json` that was created with the app's release hashes (both SHA-1 and SHA-256) should be used.
6. To setup the `google-services.json` with these hashes, go to `https://console.firebase.google.com/project/«firebase project id»/settings/general`. Click on `Add fingerprint`. When on debug, use the debug SHA's. When you're ready for a release, replace the current SHA's with the release SHA's and create a new `google-services.json`
7. Finally, the `google-services.json` file should be set as an Base64 string environment variable in Github Actions and be converted to a file that's placed in `app/` directory during the job execution (and before the gradle build step)
8. More info
   - https://developers.google.com/android/guides/client-auth
   - https://developer.android.com/studio/publish/app-signing

## Source Code Structure

![](./imgs/mermaid-digram_src_code_structure.png)

## Main challenges
- Custom and made from the ground up floating action button and it's bounds and state handling
- GeminiChat feature, state management and persistence (could be improved, per example saving the whole conversation instead of the latest message when toggling on/off Gemini)

## Things that were not done for the sake of moving on to other things
- Not supporting the animated detection outlines for more than 1 object when detection animation is enabled, since an identifier is required for the animation to track something, that being 1 object, but MediaPipe doesn't provide identifiers (and it wouldn't make sense that it did). Guessing could be done with the positioning and size of the outline, but it would be overkill
- Not worrying about still using ImageDetectionUseCase when toggling Gemini mode. Only the emission of results is stopped. This was done like this in order to not restart (and interrupt) the camera preview, it's also faster this way.
- Not displaying the whole list of Gemini messages in the chat, but only displaying the latest one
- Not opening the Achievements screen when clicking on a new achievement notification, but only navigating to the MainActivity

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

