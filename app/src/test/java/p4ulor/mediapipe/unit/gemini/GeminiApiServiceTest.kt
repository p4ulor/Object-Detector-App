package p4ulor.mediapipe.unit.gemini

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import p4ulor.mediapipe.data.utils.encodeToBase64
import p4ulor.mediapipe.data.domains.gemini.GeminiPrompt
import p4ulor.mediapipe.data.sources.gemini.GeminiApiService
import p4ulor.mediapipe.data.sources.gemini.MimeType
import p4ulor.mediapipe.utils.LoggingMock
import kotlin.test.assertTrue

@ExtendWith(LoggingMock::class)
class GeminiApiServiceTest {

    /**
     * The tests require have and require their own resources (app/src/test/resources) so
     * [javaClass.classLoader.getResource] is used to get API key and an image
     */
    @Test
    fun `Describe app logo`() = runTest {
        with(javaClass.classLoader){
            val apiKey = getResource("api_key.txt").readText().trim()!! //trim to remove next lines
            val service = GeminiApiService(apiKey)
            val prompt = GeminiPrompt(
                "Caption this image.",
                encodeToBase64(getResource("app_icon_og.png").readBytes()),
                MimeType.PNG
            )
            with(service.promptWithImage(prompt)!!){
                assertTrue(generatedText.length > 5)
                assertTrue(generatedText.contains("caption", ignoreCase = true)) // The AI can generate anything, but least one mention to this word would make sense
                assertTrue(totalTokensUsed > 10)
                println(generatedText)
            }
        }
    }
}