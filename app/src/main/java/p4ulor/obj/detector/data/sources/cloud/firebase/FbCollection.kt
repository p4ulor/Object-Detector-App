package p4ulor.obj.detector.data.sources.cloud.firebase

enum class FbCollection(val id: String) {
    Users("users"), // all users and their points
    TopUsers("top-users"), // top 5 users with the highest points
    TopObjects("top-objects")
}