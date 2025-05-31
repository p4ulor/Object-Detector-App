## Setup
In `root` npm project dir AND `functions` npm project dir
- npm install

Inside `root` npm dir
- npx firebase-tools login

## Main commands
Inside `functions/` dir:
- npm run deploy -> to deploy to Firebase's Cloud Functions, when they are ready for production
- npm run serve -> to run firebase emulator (with UI, great for testing). http://127.0.0.1:4000/
- npm run serve-saved -> to run firebase emulator in a way that saves and imports the dummy data written to it

Manual command to export data of running emulators. Inside root npm dir:
- npx firebase emulators:export dev-data

## Notes
- I'm using CommonJS, not ES Modules (ESM) importing syntax
- I'm using JavaScript just to make it simpler (and leave Typescript for other things. And experience is experience)
- If there is an error along the lines of:
    - "functions: Request to «...» had HTTP Error: 400, Validation failed for trigger «...» Invalid resource state for "": Permission denied while using the Eventarc Service Agent"
    - Go to the IAM tab in Google Cloud panel that belongs to the Firebase project `https://console.cloud.google.com/iam-admin/iam?inv=1&invt=Abxwjw&project=object-detector-7f0d6` 
    - And click the check box `Include Google-provided role grants`

## Project setup used (for future reference)
THis setup avoids installing firebase globally (without -g). Inside root npm dir:

- npm init -y
- npm install firebase-tools --save-dev
- npx firebase-tools login (credentials will be stored at `/home/username/.config/configstore`)
- npx firebase init functions
- npx firebase init firestore
- npx firebase init emulators -> select Functions Emulator and Firestore Emulator

Next, delete
- firestore:rules

and delete `"rules": "firestore.rules"` at `firebase.json` inside the `"firestore"` object. Since I want the Firestore to be the only one setting up and I don't want the deployment of each cloud function to override the current rules (which is what happens...)

Then you can also delete `firestore.indexes.json` since indexes is not used for this project
