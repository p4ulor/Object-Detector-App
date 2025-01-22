package p4ulor.mediapipe.data.sources.gemini

import p4ulor.mediapipe.data.sources.KtorClient

/**
 * Provides a clean interface for making HTTP calls to the [GeminiApiEndpoints] using a [KtorClient].
 * Use the "gemini test.sh" in docs/gemini-api-curl-test for reference.
 */
class GeminiApiService(private val apiKey: String) {
    private val http = KtorClient(GeminiApiEndpoints.hostName)

    fun uploadImageAndGetDescription(){
        // 1. Sends information about the file you want to upload. File size, MIME type, etc


        // 2. Based on the upload-headers obtained, , upload the file to the upload_url indicated by google

        // 3. Prompt Gemini along with the file_uri that's now in Google's servers
    }
}