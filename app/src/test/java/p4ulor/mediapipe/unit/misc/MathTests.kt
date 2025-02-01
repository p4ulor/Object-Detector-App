package p4ulor.mediapipe.unit.misc

import p4ulor.mediapipe.data.utils.trimToDecimals
import kotlin.test.Test
import kotlin.test.assertEquals

class MathTests {
    @Test
    fun `trimToDecimals tests`(){
        assertEquals(
            expected = 10.46.toString(),
            actual = 10.4654534f.trimToDecimals(2).toString()
        )
    }
}