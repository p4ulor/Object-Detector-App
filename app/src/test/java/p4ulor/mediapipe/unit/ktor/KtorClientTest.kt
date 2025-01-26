package p4ulor.mediapipe.unit.ktor

import io.ktor.client.call.body
import io.ktor.http.ContentType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import p4ulor.mediapipe.data.sources.KtorClient
import p4ulor.mediapipe.unit.ktor.DummyJsonPostCommentsResp.Comment
import p4ulor.mediapipe.unit.ktor.DummyJsonPostCommentsResp.User
import p4ulor.mediapipe.utils.LoggingMock
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(LoggingMock::class)
class KtorClientTest {
    private val ktorClient = KtorClient("dummyjson.com")

    @Test
    fun `Get method`() = runTest {
        assertEquals(
            expected = DummyJsonTestResp("ok", "GET"),
            actual = ktorClient.get("test")?.body()
        )
    }

    @Test
    fun `Post method`() = runTest {
        assertEquals(
            expected = DummyJsonPostResp(id = 252, title = "Post method", userId = 2),
            actual = ktorClient.post("/posts/add", body = DummyJsonPostReq("Post method", 2))?.body()
        )
    }

    @Test
    fun `Test headers and content type`() = runTest {
        assertEquals(
            expected = "application/json",
            actual = ContentType.Application.Json.toString()
        )
    }

    @Test
    fun `Test complex json response`() = runTest {
        val resp = ktorClient.get("/posts/1/comments")?.body<DummyJsonPostCommentsResp>()!!
        val expected = DummyJsonPostCommentsResp(
            comments = listOf(Comment(93, "These are fabulous ideas!", User(190, "leahw"))),
            total = 3,
            skip = 0
        )
        assertEquals(
            expected = expected.comments.first(),
            actual = resp.comments.first()
        )
        assertEquals(
            expected = expected.total,
            actual = resp.total
        )
    }
}