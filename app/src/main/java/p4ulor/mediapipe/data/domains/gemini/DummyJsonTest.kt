package p4ulor.mediapipe.data.domains.gemini

import io.ktor.resources.Resource
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

@Resource("/test")
class DummyTest()

@Resource("/posts")
class DummyPosts() {
    @Resource("add")
    class Add(val parent: DummyPosts = DummyPosts())
}
