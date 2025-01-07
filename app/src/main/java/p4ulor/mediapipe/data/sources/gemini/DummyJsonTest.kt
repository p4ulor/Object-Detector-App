package p4ulor.mediapipe.data.sources.gemini

import kotlinx.serialization.Serializable

/** https://dummyjson.com/docs#intro-test */
@Serializable
data class DummyJsonTest(
    val status: String,
    val method: String
)

/** https://dummyjson.com/docs/posts#posts-add */
@Serializable
data class DummyJsonTestPostReq(
    val title: String,
    val userId: Int
)

@Serializable
data class DummyJsonTestPostResp(
    val id: Int,
    val title: String,
    val userId: Int
)
