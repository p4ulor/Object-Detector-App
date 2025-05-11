## Setup
- npm init -y
- npm install -g firebase-tools
- npx firebase-tools login (credentials will be stored at `/home/username/.config/configstore
`)
- npx firebase init functions
- npx firebase init firestore

Next, delete
- firestore:rules

and `"rules": "firestore.rules"` at `firebase.json` inside the `"firestore"` object. Since I want the Firestore to be the only one setting up and I don't want the deployment of each cloud function to override the current rules (which is what happens...)

Then you can also delete `firestore.indexes.json` since it's not used for this project

## To deploy
- npx firebase deploy

## Notes
- its recommended to have all functions with lowercase letters
