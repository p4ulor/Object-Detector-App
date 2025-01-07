package p4ulor.mediapipe.unit

import kotlinx.coroutines.test.runTest
import p4ulor.mediapipe.data.sources.gemini.DummyJsonTestPostReq
import p4ulor.mediapipe.data.sources.KtorClient
import kotlin.test.Test

class KtorClientTest {
    private val ktorClient = KtorClient("dummyjson.com")

    @Test
    fun `Get method`() = runTest {
        println(ktorClient.get("test"))
    }

    @Test
    fun `Post method`() = runTest {
        println(ktorClient.post("/posts/add", DummyJsonTestPostReq("Post method", 2)))
    }
}