package p4ulor.obj.detector.unit.misc

import p4ulor.obj.detector.data.utils.trimToDecimals
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