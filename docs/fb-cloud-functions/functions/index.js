//const {onDocumentCreated} = require("firebase-functions/v2/firestore") // alt
const functions = require("firebase-functions/v2")
const logger = require("firebase-functions/logger")
const admin = require("firebase-admin")
const { FieldPath, FieldValue } = require("@google-cloud/firestore")

const { User, TopUser, ObjectDetectionStats, Document, UserAchievement } = require("./documents")
const { getObjectNames } = require("./utils.js")

const collections = {
    users : "users",
    topUsers: "top-users",
    topObjects : "top-objects"
}
const TOP_USERS_MAX_DOCS = 5
const TOP_OBJECTS_MAX_DOCS = 10

admin.initializeApp()
const db = admin.firestore()
db.settings({ ignoreUndefinedProperties: true }) // for testing, so we dont have to create dummy fields for everything when using firebase emulator

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

initializeTopObjects()

/** 
 * The following 3 functions listens to various changes on @see usersCollection collection and updates the 
 * @see topUsersCollection and @see topObjectsCollection
 * 
 * Object.assign() is used to set documents to go around the error:
 * - Firestore doesn't support JavaScript objects with custom prototypes 
 * (i.e. objects that were created via the "new" operator)
 * - https://stackoverflow.com/a/56921321/9375488
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
        logger.info(`Deleting user in top users ${deletedUserId}`)
        removeUserFromTopUsers(deletedUserId, topUsers)
    }

    return true
})

exports.onUserPointsUpdated = functions.firestore.onDocumentUpdated(collections.users+"/{documentId}", async (event) => {
    const snapshotBeforeUpdate = event.data.before
    const userBefore = new User(snapshotBeforeUpdate.data())

    const snapshotAfterUpdate = event.data.after
    const userId = snapshotAfterUpdate.id
    const userAfter = new User(snapshotAfterUpdate.data())

    if (userBefore.points != userAfter.points) {
        const topUsers = await getTopUsersInAscPoints()
        const topUserWithLowestPoints = topUsers[0]
        if (topUserWithLowestPoints.asTopUser().points < userAfter.points) {
            if (topUsers.length == TOP_USERS_MAX_DOCS) {
                topUsersCollection
                    .doc(topUserWithLowestPoints.uid)
                    .delete()
            }
            
            topUsersCollection
                .doc(userId)
                .set(Object.assign({}, new TopUser(userAfter.points)))
        
        } else if (topUsers.some((value) => value.uid == userId) && topUserWithLowestPoints.asTopUser().points > userAfter.points ) { // if the user is in topUsers and he resets achievements and updates his points
            removeUserFromTopUsers(userId, topUsers)
        } else {
            logger.info(`No changes to ${collections.topUsers}`)
        }
    }

    await updateTopObjects(userBefore.achievements, userAfter.achievements)

    return true
})

async function getTopUsersInAscPoints(){
    const snapshot = await topUsersCollection
        .orderBy("points", "asc")
        .limit(TOP_USERS_MAX_DOCS)
        .get()
    
    return snapshot.docs.map(doc => {
        return new Document(doc.id, new TopUser(doc.data().points))
    })
}

/**
 * Removes a user from the @see {topUsersCollection} and looks for a new user to take its place
 * @param {string} userId the user to remove
 * @param {Document[]} topUsers the current top users
 */
async function removeUserFromTopUsers(userId, topUsers){
    await topUsersCollection
        .doc(userId)
        .delete()
    
    //now, the expected lenght of topUsers should be 4, get the 5th user with the most points
    const eligibleUserQuery = await usersCollection
        .where(FieldPath.documentId(), "not-in", topUsers.map(value => value.uid))
        .orderBy("points", "desc")
        .limit(1)
        .get()
    
    if (!eligibleUserQuery.empty) {
        // Get the user with the most points who is not in the current topUsers
        const eligibleUserDoc = eligibleUserQuery.docs[0]
        const newTopUser = new User(eligibleUserDoc.data())

        await topUsersCollection
            .doc(eligibleUserDoc.id)
            .set(Object.assign({}, newTopUser))

        logger.info(`Replaced user ${userId} with ${eligibleUserDoc.id}`)
    } else {
        logger.info(`No eligible replacement user found for ${userId}`)
    }
}

/**
 * @param {Array<UserAchievement>} achievementsBefore 
 * @param {Array<UserAchievement>} achievementsAfter 
 */
async function updateTopObjects(achievementsBefore, achievementsAfter) {
    if (achievementsBefore.length != achievementsAfter.length) {
        logger.info(`achievementsBefore ${achievementsBefore.length} and achievementsAfter ${achievementsAfter.length} are not of equal length, this is expected when a user is submitting his achievements for the first time or when testing (users should have an array of fixed lenght (80) in this field )`)
    }

    // Sort both arrays by objectName to ensure matching order
    achievementsBefore.sort((a, b) => a.objectName.localeCompare(b.objectName))
    achievementsAfter.sort((a, b) => a.objectName.localeCompare(b.objectName))

    for(let i = 0; i < achievementsAfter.length; i++) {

        let before = achievementsBefore[i]
        const after = achievementsAfter[i]

        if (before == undefined) { // then the user hasen't submitted before. Thus achievementsBefore.length == 0
            before = new UserAchievement(after.objectName, 0)
        }

        if (before.objectName !== after.objectName) {
            logger.error(`Mismatched object names at index ${i}: ${before.objectName} vs ${after.objectName}`)
            continue
        }

        if (before.certaintyScore != after.certaintyScore && after.certaintyScore != 0.0) {
            const detectionCount = ObjectDetectionStats.DETECTION_COUNT_FIELD
            topObjectsCollection
                .doc(after.objectName)
                .update({[detectionCount]: FieldValue.increment(1)})
                .catch(e => {
                    logger.error(`Error incrementing obj count (the objectName ${after.objectName} may be invalid). Code: ${e.code}. Details: ${e.details}`)
                })

            logger.info(`Incremented count for ${after.objectName}`)
        }
    }
}

/** 
 * Will be triggered when accessing the firebase function through HTTP like
 * https://helloworld-m7or7p2m4q-uc.a.run.app
 */
/* exports.helloWorld = functions.https.onRequest((request, response) => {
    console.log("test")
    logger.info("test2")
    response.send("<h1>Hello from Firebase!</h1>")
}) */

async function initializeTopObjects() {
    const docsInTopObjects = (await topObjectsCollection.count().get()).data().count
    logger.info("docsInTopObjects", docsInTopObjects)
    if (docsInTopObjects <= 1) { // because if a collec has no docs it's deleted...
        const objectNames = await getObjectNames()
        const batch = db.batch()

        for (const name of objectNames) {
            const docRef = topObjectsCollection.doc(name)
            const detectionCount = ObjectDetectionStats.DETECTION_COUNT_FIELD
            batch.set(docRef, { [detectionCount]: 0 })
        }

        await batch.commit()
        logger.info("topObjects collection initialized")
    } else {
        logger.info("topObjects collection already initialized")
    }
}