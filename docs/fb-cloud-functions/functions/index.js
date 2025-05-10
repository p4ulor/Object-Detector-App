/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * https://firebase.google.com/docs/functions
 */

const {
    onDocumentWritten,
    onDocumentCreated,
    onDocumentUpdated,
    onDocumentDeleted,
    Change,
    FirestoreEvent
  } = require("firebase-functions/v2/firestore")

const functions = require("firebase-functions")
const logger = require("firebase-functions/logger")

exports.helloWorld = functions.https.onRequest((request, response) => {
    console.log("test")
    logger.info("test2")
    response.send("Hello from Firebase!")
})

