/** These are used to define a structure to the firestore documents and for intellisense */

/**
 * The uid should be the uid of the FirebaseUser
 * @typedef {Object} QueryDocumentSnapshot
 * @property {string} name
 * @property {string} photoUri
 * @property {number} points
 * @property {Array<UserAchievement>} achievements
 */
class User {
    /** @param {QueryDocumentSnapshot} snapshot */
    constructor(snapshot) {
        this.name = snapshot.name
        this.photoUri = snapshot.photoUri
        this.points = snapshot.points
        this.achievements = snapshot.achievements
    }

    static POINTS_FIELD = "points"
}

class UserAchievement {
    /** @type {string} */
    objectName
    /** @type {number} */
    certaintyScore

    constructor(objectName, certaintyScore) {
        this.objectName = objectName
        this.certaintyScore = certaintyScore
    }
}

/** The uid should be the uid of the FirebaseUser */
class TopUser {
    /** @type {number} */
    points

    constructor(points) {
        this.points = points
    }  
}

/** The uid should be the objectName */
class ObjectDetectionStats {
    /** @type {number} */
    detectionCount

    constructor(detectionCount) {
        this.detectionCount = detectionCount
    }
}

/** Represents a document (it's uid and it's data) */
class Document {
    /** @type {string} */
    uid
    /**  @type {User | TopUser | ObjectDetectionStats} data */
    data

    constructor(uid, data) {
        this.uid = uid
        this.data = data
    }

    /** For Intelissense */

    /** @return {User} */
    asUser() { return this.data }

    /** @return {TopUser} */
    asTopUser() { return this.data }

    /** @return {ObjectDetectionStats} */
    asObjectDetectionStats() { return this.data }
}

module.exports = { User, TopUser, ObjectDetectionStats, Document }