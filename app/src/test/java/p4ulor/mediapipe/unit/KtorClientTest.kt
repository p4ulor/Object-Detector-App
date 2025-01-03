package p4ulor.mediapipe.unit

import kotlinx.coroutines.test.runTest
import p4ulor.mediapipe.data.domains.gemini.DummyJsonTestPostReq
import p4ulor.mediapipe.data.domains.gemini.DummyTest
import p4ulor.mediapipe.data.sources.KtorClient
import kotlin.test.Test

class KtorClientTest {
    private val ktorClient = KtorClient("dummyjson.com")

    @Test
    fun `Get method`() = runTest {
        println(ktorClient.get("test"))
        //println(ktorClient.get(DummyTest()))
    }

    @Test
    fun `Post method`() = runTest {
        println(ktorClient.post("/posts/add", DummyJsonTestPostReq("Post method", 2)))
    }
}