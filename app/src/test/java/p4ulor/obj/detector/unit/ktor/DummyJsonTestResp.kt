package p4ulor.obj.detector.unit.ktor

import kotlinx.serialization.Serializable

/** https://dummyjson.com/docs#intro-test */
@Serializable
data class DummyJsonTestResp(
    val status: String,
    val method: String
)

/** https://dummyjson.com/docs/posts#posts-add */
@Serializable
data class DummyJsonPostReq(
    val title: String,
    val userId: Int
)

@Serializable
data class DummyJsonPostResp(
    val id: Int,
    val title: String,
    val userId: Int
)

@Serializable
data class DummyJsonPostCommentsResp(
    val comments: List<Comment>,
    val total: Int,
    val skip: Int
) {
    @Serializable
    data class Comment(
        val id: Int,
        val body: String,
        val user: User
    )

    @Serializable
    data class User(
        val id: Int,
        val username: String
    )
}

