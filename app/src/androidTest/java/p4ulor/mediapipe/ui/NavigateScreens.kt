package p4ulor.mediapipe.ui

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.activities.MainActivity
import p4ulor.mediapipe.ui.components.utils.UiTestTag
import p4ulor.mediapipe.ui.screens.root.RootScreen
import p4ulor.mediapipe.ui.screens.root.Screen
import p4ulor.mediapipe.ui.theme.AppTheme

/**
 *  Instrumented test (AKA UI tests), which will execute on an Android device.
 *  See [testing documentation](http://d.android.com/tools/testing).
 */
class NavigateScreens {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val activity = createAndroidComposeRule<MainActivity>()

    @Rule
    @JvmField
    val grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources

    @Test
    fun navigateTheMain3Screens(): Unit = with(composeTestRule) {
        setContent {
            AppTheme {
                RootScreen()
            }
        }

        onNodeWithTag(UiTestTag.bottomAppBar)
            .assertIsDisplayed()

        onNodeWithText(Screen.Home.name)
            .assertIsDisplayed()

        onNodeWithContentDescription(Screen.Settings.name)
            .performClick()

        onNodeWithText(resources.getString(R.string.gemini_api)) // activity can't be used because I get "Cannot run onActivity since Activity has been destroyed already"
            .assertExists()

        onNodeWithContentDescription(Screen.Achievements.name)
            .performClick()
    }
}
