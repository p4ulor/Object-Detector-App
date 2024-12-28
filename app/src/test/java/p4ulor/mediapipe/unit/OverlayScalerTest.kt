package p4ulor.mediapipe.unit

import android.graphics.RectF
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import p4ulor.mediapipe.ui.screens.home.OverlayScaler
import kotlin.test.assertEquals

/**
 * Using mockk because to instantiate Android classes in tests
 * https://developer.android.com/training/testing/local-tests#mocking-dependencies
 */
class OverlayScalerTest {
    @Test
    fun `Scale width and height from 1920x1080 to 1280x720`() {
        val scaler = OverlayScaler(
            frameWidth = 1920,
            frameHeight = 1080,
            containerWidth = 1280f,
            containerHeight = 720f
        )

        val res4by3 = mockk<RectF>(relaxed = true) {
            every { width() } returns 1600f
            every { height() } returns 900f

            // not working in mockk https://github.com/mockk/mockk/issues/1291 https://github.com/mockk/mockk/issues/104
            // every { left } returns 0f // error: Missing mocked calls inside every { ... } block: make sure the object inside the block is a mock
            // every { getProperty("left") } propertyType java.lang.Float::class answers { 0f } //
        }

        val res16by9 = mockk<RectF> {
            every { width() } returns 1024f
            every { height() } returns 576f
        }

        val box4by3 = scaler.scaleBox(res4by3)
        val box16by9 = scaler.scaleBox(res16by9)

        with(res4by3){
            assertEquals(
                expected = (width() / height()).round(6),
                actual = (box4by3.width / box4by3.height).round(6)
            )
        }

        with(res16by9){
            assertEquals(
                expected = (width() / height()).round(6),
                actual = (box16by9.width / box16by9.height).round(6)
            )
        }
    }

    fun Float.round(decimals: Int): Float {
        var multiplier = 1.0f
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}

