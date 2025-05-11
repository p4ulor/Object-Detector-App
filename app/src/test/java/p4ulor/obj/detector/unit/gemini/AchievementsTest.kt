package p4ulor.obj.detector.unit.gemini

import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.domains.mediapipe.calculatePoints
import kotlin.test.Test
import kotlin.test.assertEquals

class AchievementsTest {
    @Test
    fun `calculatePoints works`() {
        val achievements = listOf(
            Achievement("obj1", 0.4f, null),
            Achievement("obj2", 0.3f, null),
            Achievement("obj3", 0.3f, null)
        )
        assertEquals(expected = 1f, actual = achievements.calculatePoints())
    }
}