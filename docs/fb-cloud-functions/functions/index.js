//const {onDocumentCreated} = require("firebase-functions/v2/firestore") // alt
const functions = require("firebase-functions/v2")
const logger = require("firebase-functions/logger")
const admin = require('firebase-admin')

const { User, TopUser, ObjectDetectionStats, Document } = require("./documents")

const collections = {
    users : "users",
    topUsers: "top-users",
    topObjects : "top-objects"
}
const TOP_USERS_MAX_DOCS = 5
const TOP_OBJECTS_MAX_DOCS = 10

admin.initializeApp()
const db = admin.firestore()

/** Contains documents with structure @see User */
const usersCollection = db.collection(collections.users)
/** 
 * Contains documents with structure @see TopUser
 * It is worth having the value of the points duplicated for the cost of the queries I would otherwise perform.
 * Per example if this collection only had documents whose uid points to a user to avoid the duplication, I would 
 * have to query each user out of the user collection just to get their points. I have more flexibility like this
 */
const topUsersCollection = db.collection(collections.topUsers)
/** Contains documents with structure @see ObjectDetectionStats */
const topObjectsCollection = db.collection(collections.topObjects)

/** 
 * The following 3 functions listens to various changes on @see usersCollection collection and updates the 
 * @see topUsersCollection and @see topObjectsCollection
 * 
 * Object.assign() is used to set documents to go around the error:
 * - Firestore doesn't support JavaScript objects with custom prototypes 
 * (i.e. objects that were created via the 'new' operator)
 * 
 * ## Resources
 * - https://firebase.google.com/docs/functions/firestore-events?hl=en&authuser=0&gen=2nd
 */
exports.onUserCreated = functions.firestore.onDocumentCreated(collections.users+"/{documentId}", async (event) => {
    const snapshot = event.data
    const newUserId = snapshot.id // or event.params.documentId
    const newUser = new User(snapshot.data())

    logger.info(`Created user ${newUserId}`)

    const topUsers = await getTopUsersInAscPoints()
    const topUsersLog = topUsers.map(value => {
        return {
            id: value.id, 
            points: value.asTopUser().points
        }
    })
    .map(obj => JSON.stringify(obj))
    .join(", ")  
    logger.info(`topUsersLog = ${topUsersLog}`)
    
    if (topUsers.length < TOP_USERS_MAX_DOCS) {
        topUsersCollection
            .doc(newUserId)
            .set(Object.assign({}, new TopUser(newUser.points)))
    } else {
        if (topUsers[0].asTopUser().points < newUser.points) {
            topUsersCollection
                .doc(topUsers[0].uid)
                .delete()
            
            topUsersCollection
                .doc(newUserId)
                .set(Object.assign({}, new TopUser(newUser.points)))
        } else {
            logger.info(`No changes to ${collections.topUsers}`)
        }
    }

    return true
})

exports.onUserDeleted = functions.firestore.onDocumentDeleted(collections.users+"/{documentId}", async (event) => {
    const snapshot = event.data
    const deletedUserId = snapshot.id
    const deletedUser = new User(snapshot.data())

    const topUsers = await getTopUsersInAscPoints()
    const isDeletedUserInTopUsers = topUsers.some(value => value.uid == deletedUserId)
    if (isDeletedUserInTopUsers) {
        topUsersCollection
                .doc(deletedUserId)
                .delete()

        // todo get user with most points, not already in top users
    }

    return true
})

exports.onUserPointsUpdated = functions.firestore.onDocumentUpdated(collections.users+"/{documentId}", async (event) => {
    const snapshotPostUpdate = event.data.after
    const userId = snapshotPostUpdate.id
    const user = new User(snapshotPostUpdate.data())

    const topUsers = await getTopUsersInAscPoints()

    return true
})

async function getTopUsersInAscPoints(){
    const snapshot = await topUsersCollection
        .orderBy('points', 'asc')
        .limit(TOP_USERS_MAX_DOCS)
        .get()
    
    return snapshot.docs.map(doc => {
        return new Document(doc.id, new TopUser(doc.data().points))
    })
}

async function getEligibleUserForTopUsers(minimum){

}

/** Will be triggered when accessing the firebase function through HTTP like so */
exports.helloWorld = functions.https.onRequest((request, response) => {
    console.log("test")
    logger.info("test2")
    response.send("<h1>Hello from Firebase!</h1>")
})
