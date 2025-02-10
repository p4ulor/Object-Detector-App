package p4ulor.mediapipe.ui.components.utils

import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController

/**
 * Gets the height system's navigation bar (recent items, home, back). Useful to setting the height
 * of the app's bottomBar when using [enableEdgeToEdge]. And this method seems kinda blocking
 */
val SystemNavigationBarHeight
    @Composable
    get() = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

/** Gets [Dp] size of a text (considering the default text size) */
@Composable
fun getTextSize(string: String): Dp {
    val textMeasurer = rememberTextMeasurer()
    return with(LocalDensity.current) { textMeasurer.measure(string).size.width.toDp() }
}

val DisplayHeight: Dp
    @Composable
    get() = run {
        val display = LocalContext.current.resources.displayMetrics
        with(LocalDensity.current) { display.heightPixels.toDp() }
    }

val NavHostController.currentRoute
    get() = currentBackStackEntry?.destination?.route

val NavHostController.previousRoute
    get() = previousBackStackEntry?.destination?.route