package p4ulor.mediapipe

import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import p4ulor.mediapipe.android.activities.MainActivity
import p4ulor.mediapipe.ui.screens.root.RootScreen
import p4ulor.mediapipe.ui.theme.AppTheme

class NavigateScreens {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val activity = createAndroidComposeRule<MainActivity>()

    @Test
    fun myTest() {
        // Start the app
        composeTestRule.setContent {
            AppTheme {
                //activity.enableEdgeToEdge()
                RootScreen()
            }
        }

        composeTestRule.onNodeWithText("Continue").performClick()

        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
    }
}