package p4ulor.mediapipe.utils

import android.util.Log
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Should be used for tests that call logs, so that [Log] is not called, since it's not available in
 * [test environments](https://developer.android.com/training/testing/local-tests#mocking-dependencies)
 * Test classes that use these tests should use [ExtendWith] annotation and pass [this::class]
 * and have it's tests use the annotation [org.junit.jupiter.api.Test]
 */
class LoggingMock : BeforeAllCallback, AfterAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } answers { println(secondArg<String>()); 0 }
        every { Log.e(any(), any()) } answers { println(secondArg<String>()); 0 }
    }

    override fun afterAll(context: ExtensionContext) {}
}