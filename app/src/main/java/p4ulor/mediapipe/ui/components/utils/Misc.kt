package p4ulor.mediapipe.ui.components.utils

import android.content.res.Resources
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import p4ulor.mediapipe.ui.screens.root.BottomNavigationBarHeight

/**
 * Gets the height of the system's navigation bar (recent items, home, back). Useful to setting
 * the height of the app's bottomBar when using [enableEdgeToEdge]. And this method seems to be
 * kinda blocking
 */
val SystemNavigationBarHeight
    @Composable
    get() = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

/** Gets [Dp] size of a text (considering the default text size) */
@Composable
fun textWidthOf(string: String): Dp {
    val textMeasurer = rememberTextMeasurer()
    return with(LocalDensity.current) { textMeasurer.measure(string).size.width.toDp() }
}

val DisplayHeight: Dp
    @Composable
    get() = run {
        val display = LocalContext.current.resources.displayMetrics
        with(LocalDensity.current) { display.heightPixels.toDp() }
    }

/** The height of a screen, that's inside a route, excluding the [BottomNavigationBarHeight] */
val ScreenHeight: Dp
    @Composable
    get() = DisplayHeight - BottomNavigationBarHeight

val ScreenCenter: IntOffset
    get() {
        val displayMetrics = Resources.getSystem().displayMetrics // alt to using Context.resources.displayMetrics
        val centerX = displayMetrics.widthPixels / 2
        val centerY = displayMetrics.heightPixels / 2
        return IntOffset(centerX, centerY)
    }

val NavHostController.currentRoute
    get() = currentBackStackEntry?.destination?.route

val NavHostController.previousRoute
    get() = previousBackStackEntry?.destination?.route