package p4ulor.mediapipe.unit

import io.ktor.http.ContentType
import kotlinx.coroutines.test.runTest
import p4ulor.mediapipe.data.sources.gemini.DummyJsonTestPostReq
import p4ulor.mediapipe.data.sources.KtorClient
import p4ulor.mediapipe.data.sources.gemini.DummyJsonTest
import p4ulor.mediapipe.data.sources.gemini.DummyJsonTestPostResp
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorClientTest {
    private val ktorClient = KtorClient("dummyjson.com")

    @Test
    fun `Get method`() = runTest {
        assertEquals(
            expected = DummyJsonTest("ok", "GET"),
            actual = ktorClient.get("test")
        )
    }

    @Test
    fun `Post method`() = runTest {
        assertEquals(
            expected = DummyJsonTestPostResp(id = 252, title = "Post method", userId = 2),
            actual = ktorClient.post("/posts/add", DummyJsonTestPostReq("Post method", 2))
        )
    }

    @Test
    fun `Test headers and content type`() = runTest {
        assertEquals(
            expected = "application/json",
            actual = ContentType.Application.Json.toString()
        )
    }
}