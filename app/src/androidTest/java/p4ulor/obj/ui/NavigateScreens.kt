package p4ulor.obj.ui

import android.Manifest
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.swipeRight
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import p4ulor.obj.detector.R
import p4ulor.obj.detector.android.activities.MainActivity
import p4ulor.obj.detector.ui.components.utils.UiTestTag
import p4ulor.obj.detector.ui.screens.root.RootScreen
import p4ulor.obj.detector.ui.screens.root.Screen
import p4ulor.obj.detector.ui.theme.AppTheme

/**
 * Instrumented test (AKA UI tests), which will execute on an Android device.
 * See [testing documentation](http://d.android.com/tools/testing).
 * Notes:
 * - Currently, Junit4 must be used for instrumented tests
 * - Apparently the tests are run alphabetically by default. So a_navigateTheMain3Screens
 * is named like it is to run correctly, although the tests are independent. So
 * @FixMethodOrder(MethodSorters.NAME_ASCENDING) is not needed
 * - There's some problems when the loading of the UI in some circumstances. Which seems to happen
 * when per example the Settings screen is opened in 1 test, and when it is opened again in the next
 * test, its content is not visible, causing the test to fail.
 */
class NavigateScreens {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val activity = createAndroidComposeRule<MainActivity>()

    @Rule
    @JvmField
    val grantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA,
        Manifest.permission.POST_NOTIFICATIONS
    )

    private val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources

    @Test
    fun a_changeSomeSettings(): Unit = with(composeTestRule) {
        runTestAndCleanUp(
            test = {
                initRootScreen()

                onNodeWithTag(UiTestTag.bottomAppBar)
                    .assertIsDisplayed()

                goToScreen(Screen.Settings)

                onNodeWithText(resources.getString(R.string.gemini_api)) // fails when some other test runs before this one, probably an error from the compose libs
                    .assertIsDisplayed()

                onNodeWithTag(UiTestTag.settingsMinimumDetectionCertaintyValue)
                    .assertTextEquals("50%")

                onNodeWithTag(UiTestTag.settingsMinimumDetectionCertaintySlider)
                    .performTouchInput {
                        swipeRight( // Drag to 100%
                            startX = this.width.toFloat() / 2, // starting in the middle, from the default 50%
                            endX = this.width.toFloat() + 20, // +20 to make sure it reaches 100%, not 99%
                            durationMillis = 500
                        )
                    }

                waitForIdle()

                onNodeWithTag(UiTestTag.settingsMinimumDetectionCertaintyValue)
                    .printToLog("DEBUG_settingsMinimumDetectionCertainty")

                onNodeWithTag(UiTestTag.settingsMinimumDetectionCertaintyValue)
                    .assertTextEquals("100%")

                onNodeWithTag(UiTestTag.settingsCheckBox)
                    .assertIsOff()
                    .performClick()

                goThroughAnimationsOrStateChange()

                onNodeWithTag(UiTestTag.settingsCheckBox)
                    .assertIsOn()
            },
            cleanup = {
                onNodeWithTag(UiTestTag.settingsMinimumDetectionCertaintySlider)
                    .performTouchInput {
                        click(this.center) // puts slider at 50%
                    }

                waitForIdle()

                val checkboxNode = onNodeWithTag(UiTestTag.settingsCheckBox)
                val checkboxIsOn = checkboxNode
                    .fetchSemanticsNode()
                    .config[SemanticsProperties.ToggleableState] == ToggleableState.On
                if (checkboxIsOn) {
                    checkboxNode.performClick()
                }
            }
        )
    }

    @Test
    fun b_scrollAchievementsAndChangeTabs(): Unit = with(composeTestRule) {
        initRootScreen()

        goToScreen(Screen.Achievements)

        onNodeWithTag(UiTestTag.achievementsList)
            .assert(hasScrollAction())
            .performScrollToIndex(10)

        onNodeWithText(resources.getString(R.string.leaderboard))
            .performClick()

        goThroughAnimationsOrStateChange()

        onNodeWithTag(UiTestTag.achievementsGoogleSignIn)
            .assertIsDisplayed()
    }

    private fun ComposeContentTestRule.initRootScreen() {
        setContent {
            AppTheme {
                RootScreen()
            }
        }
        goThroughAnimationsOrStateChange()
    }

    private fun ComposeContentTestRule.goThroughAnimationsOrStateChange() {
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(500L)
        mainClock.autoAdvance = true
    }

    /**
     * [onNodeWithContentDescription] is used for screen nav since the name of the enum is used in
     * the contentDescription of the NavBar icons at [BuildNavigationBarItem]
     */
    private fun ComposeContentTestRule.goToScreen(screen: Screen){
        onNodeWithContentDescription(screen.name)
            .performClick()

        goThroughAnimationsOrStateChange()
    }

    private fun runTestAndCleanUp(test: () -> Unit, cleanup: () -> Unit){
        try {
            test()
        } catch (e: Exception) {
            throw e
        } finally {
            cleanup()
        }
    }
}
