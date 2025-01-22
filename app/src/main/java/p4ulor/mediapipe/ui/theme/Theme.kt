package p4ulor.mediapipe.ui.theme

import android.os.Build
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.CenteredRow

/**
 * Run in interactive mode to see the animations
 * Containers are: boxes, columns, rows, scaffold, cards and others
 */

/**
 * High contrast color to use in UI segments that haven't been figured out what color should have
 * or what they even are
 */
private val unset = Color(0xFF2FFF00)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0073FF), // Primary color used for prominent components like buttons, sliders, radio buttons, and important text.
    onPrimary = Color(0xFFFFFFFF), // For text/icons displayed on top of the primary color
    primaryContainer = Color(0xFF005BBB), // A tonal variation of the primary color for containers
    onPrimaryContainer = Color(0xFFFFFFFF), // For text/icons on primary containers
    inversePrimary = Color(0xFFBB86FC), // Inverse of the primary color for contrasting UI elements

    secondary = Color(0xFA004184), // Secondary color used for less prominent components
    onSecondary = Color(0xFFB1B1B1), // Color for text/icons displayed on top of the secondary color
    secondaryContainer = Color(0xFF2196F3), // A tonal variation of the secondary color for containers
    onSecondaryContainer = Color(0xFFFFFFFF), // Color for text/icons on secondary containers

    tertiary = Color(0x00FFFFFF), // Used for accents or highlights
    onTertiary = Color(0xFF888888), // For text/icons displayed on top of the tertiary color
    tertiaryContainer = Color(0xFF64DD17), // A tonal variation of the tertiary color for containers
    onTertiaryContainer = Color(0xFFFFFFFF), // Color for text/icons on tertiary containers

    background = Color(0xFA5A5E24), // Background color for screens and larger components
    onBackground = Color(0xFFFFFFFF), // Color for text/icons displayed on top of the background color

    surface = Color(0xFF151313), // For surfaces, cards and menus
    onSurface = Color(0xFFDBDBDB), // For text/icons displayed on top of the surface color
    surfaceVariant = Color(0xFF303030), // Another surface color variant for differentiation
    onSurfaceVariant = Color(0xFF000000), // For text/icons on surface variant
    surfaceTint = Color(0xFF303030), // Overlay color for elevated surfaces to create a tint effect

    inverseSurface = unset, // Inverse of the surface color for contrasting UI elements
    inverseOnSurface = unset, // For text/icons on inverse surfaces

    error = Color(0xFFB00020), // Color used to indicate errors.
    onError = Color(0xFFFFA8A8), // Color for text/icons displayed on top of the error color.
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color(0xFF2E000B),

    outline = Color(0xFFA1A1A1), // For outlines/borders of OutlinedTextField and OutlinedButton
    outlineVariant = Color(0xFFDFDFDF), // For HorizontalDivider

    scrim = Color(0xFF737373), // Applied on the background of unfocused content, when some popup/dialog is shown

    surfaceBright = unset,
    surfaceContainer = unset,
    surfaceContainerHigh = unset,
    surfaceContainerHighest = Color(0xFF007CD7),
    surfaceContainerLow = unset,
    surfaceContainerLowest = unset,
    surfaceDim = unset,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0073FF), // Primary color used for prominent components like buttons, sliders, radio buttons, and important text.
    onPrimary = Color(0xFFFFFFFF), // For text/icons displayed on top of the primary color
    primaryContainer = Color(0xFF005BBB), // A tonal variation of the primary color for containers
    onPrimaryContainer = Color(0xFFFFFFFF), // For text/icons on primary containers
    inversePrimary = Color(0xFFBB86FC), // Inverse of the primary color for contrasting UI elements

    secondary = Color(0xFA004184), // Secondary color used for less prominent components
    onSecondary = Color(0xFFB1B1B1), // Color for text/icons displayed on top of the secondary color
    secondaryContainer = Color(0xFF2196F3), // A tonal variation of the secondary color for containers
    onSecondaryContainer = Color(0xFFFFFFFF), // Color for text/icons on secondary containers

    tertiary = Color(0xFF71BF00), // Used for accents or highlights
    onTertiary = Color(0xFF888888), // For text/icons displayed on top of the tertiary color
    tertiaryContainer = Color(0xFF64DD17), // A tonal variation of the tertiary color for containers
    onTertiaryContainer = Color(0xFFFFFFFF), // Color for text/icons on tertiary containers

    background = Color(0xFA5A5E24), // Background color for screens and larger components
    onBackground = Color(0xFFFFFFFF), // Color for text/icons displayed on top of the background color

    surface = Color(0xFFDEDEDE), // For surfaces, cards and menus
    onSurface = Color(0xFFDBDBDB), // For text/icons displayed on top of the surface color
    surfaceVariant = Color(0xFF303030), // Another surface color variant for differentiation
    onSurfaceVariant = Color(0xFF000000), // For text/icons on surface variant
    surfaceTint = Color(0xFF303030), // Overlay color for elevated surfaces to create a tint effect

    inverseSurface = unset, // Inverse of the surface color for contrasting UI elements
    inverseOnSurface = unset, // For text/icons on inverse surfaces

    error = Color(0xFFB00020), // Color used to indicate errors.
    onError = Color(0xFFFFA8A8), // Color for text/icons displayed on top of the error color.
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color(0xFF2E000B),

    outline = Color(0xFF313131), // For outlines/borders of OutlinedTextField and OutlinedButton
    outlineVariant = Color(0xFF000000), // For HorizontalDivider

    scrim = Color(0xFF737373), // Applied on the background of unfocused content, when some popup/dialog is shown

    surfaceBright = unset,
    surfaceContainer = unset,
    surfaceContainerHigh = unset,
    surfaceContainerHighest = Color(0xFF007CD7),
    surfaceContainerLow = unset,
    surfaceContainerLowest = unset,
    surfaceDim = unset,
)

@Composable
private fun SampleComposable(){
    Surface {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("Text", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            HorizontalDivider()
            HorizontalDivider(thickness = 10.dp)

            OutlinedTextField(value = "Input", onValueChange = {}, label = { Text("Label") })

            Button(onClick = {}) {
                Text("Button")
            }
            OutlinedButton(onClick = {}) {
                Text("Outlined Button")
            }
            TextButton(onClick = {}) {
                Text("Text Button")
            }

            CenteredRow {
                var isChecked by remember { mutableStateOf(true) }
                RadioButton(selected = isChecked, onClick = { isChecked = !isChecked })
                Text("Radio Button")
            }

            CenteredRow {
                var isChecked by remember { mutableStateOf(true) }
                Checkbox(checked = isChecked, onCheckedChange = { isChecked = !isChecked })
                Text("Checkbox")
            }

            var isChecked by remember { mutableStateOf(true) }
            Switch(checked = isChecked, onCheckedChange = { isChecked = !isChecked })

            var slider by remember { mutableFloatStateOf(0.5f) }
            Slider(value = slider, onValueChange = { slider = it })

            val animatedProgress = rememberInfiniteTransition("").animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = smooth(3000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "animatedProgress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress.value },
            )

            var counter by remember { mutableIntStateOf(1) }
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text(counter.toString())
                LaunchedEffect(Unit) {
                    while(true) {
                        if(counter==3) counter = 0
                        else counter++
                        delay(1000)
                    }
                }
            }

            Card(elevation = CardDefaults.outlinedCardElevation(defaultElevation = 5.dp)) {
                Column(Modifier.padding(8.dp)) {
                    Text("Card Title", style = MaterialTheme.typography.titleMedium)
                    Text("Card Content")
                }
            }

            Icon(
                Icons.Filled.Add,
                "Add",
                Modifier.size(50.dp),
                MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview
@Composable
private fun LightColorSchemePreview() = AppTheme(enableDarkTheme = false){
    SampleComposable()
}

@Preview
@Composable
private fun DarkColorSchemePreview() = AppTheme(enableDarkTheme = true){
    SampleComposable()
}

/** https://developer.android.com/develop/ui/compose/designsystems/material3 */
@Composable
fun AppTheme(
    enableDarkTheme: Boolean = isSystemInDarkTheme(),
    useSystemDefault: Boolean = false, //only available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useSystemDefault && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (enableDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        enableDarkTheme -> darkColorScheme()
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}