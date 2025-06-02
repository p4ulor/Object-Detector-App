package p4ulor.obj.detector.ui.theme

import android.os.Build
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import p4ulor.obj.detector.R
import p4ulor.obj.detector.ui.animations.smooth
import p4ulor.obj.detector.ui.components.DropdownOptions
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.QuickText
import p4ulor.obj.detector.ui.components.utils.BoxWithBackground
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.RoundRectangleShape
import p4ulor.obj.detector.ui.components.utils.roundMessageBox
import p4ulor.obj.detector.ui.screens.achievements.Tab

/**
 * Run the previews in interactive mode to see the animations.
 * I don't put the colors in variables, so it becomes easier to edit and understand
 * Containers are: boxes, columns, rows, scaffold, cards and others
 */

/**
 * High contrast color to use in UI segments that haven't been figured out what color should have
 * or what they even are
 */
private val unset = Color(0xFF2FFF00)
private val unset2 = Color(0xFFFF0000)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0073FF), // Primary color used for prominent components like buttons, sliders, radio buttons, and important text.
    onPrimary = Color(0xFFFFFFFF), // For text/circle widget (in switch)/checkmark displayed on top of the primary color
    primaryContainer = Color(0xFF005BBB), // A tonal variation of the primary color for containers
    onPrimaryContainer = Color(0xFFFFFFFF), // For text/icons on primary containers
    inversePrimary = unset2, // Inverse of the primary color for contrasting UI elements

    secondary = Color(0xAA6797C6), // Secondary color used for less prominent components
    onSecondary = Color(0xFFFFFFFF), // Color for text/icons displayed on top of the secondary color
    secondaryContainer = Color(0x77004490), // A tonal variation of the secondary color for containers
    onSecondaryContainer = Color(0xFFFFFFFF), // Color for text/icons on secondary containers

    tertiary = unset2, // Used for accents or highlights
    onTertiary = unset2, // For text/icons displayed on top of the tertiary color
    tertiaryContainer = Color(0xC21A3C60), // A tonal variation of the tertiary color for containers
    onTertiaryContainer = Color(0xFFFFFFFF), // Color for text/icons on tertiary containers

    background = Color(0xFA5A5E24), // Background color for screens and larger components
    onBackground = unset2, // Color for text/icons displayed on top of the background color

    surface = Color(0xFF151313), // For surfaces, cards, menus and tabs
    onSurface = Color(0xFFFFFFFF), // For text/icons displayed on top of the surface color
    surfaceVariant = unset2, // Another surface color variant for differentiation
    onSurfaceVariant = Color(0xFFFFFFFF), // For "Label" in labelled boxes, icons and surface variants
    surfaceTint = unset2, // Overlay color for elevated surfaces to create a tint effect

    inverseSurface = Color(0xFFE1E1E1), // Inverse of the surface color for contrasting UI elements. Used in ToolTip
    inverseOnSurface = Color.Black, // For text/icons on inverse surfaces. Used in ToolTip text

    error = Color(0xFFB00020), // Color used to indicate errors.
    onError = Color(0xFFFFA8A8), // Color for text/icons displayed on top of the error color.
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color(0xFF2E000B),

    outline = Color(0xFF36424B), // For outlines/borders of OutlinedTextField and OutlinedButton
    outlineVariant = Color(0xFFDFDFDF), // For HorizontalDivider

    scrim = Color(0xFF737373), // Applied on the background of unfocused content, when some popup/dialog is shown

    surfaceBright = unset2,
    surfaceContainer = Color(0xFF262E37), // Used in dropdown menus
    surfaceContainerHigh = Color(0xC21A3C60), // Used for AlertDialogs
    surfaceContainerHighest = Color(0xFF007CD7), //for card
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

    secondary = Color(0xFA0E1A24), // Secondary color used for less prominent components
    onSecondary = Color(0xFFB1B1B1), // Color for text/icons displayed on top of the secondary color
    secondaryContainer = Color(0x77004490), // A tonal variation of the secondary color for containers
    onSecondaryContainer = Color(0xFFFFFFFF), // Color for text/icons on secondary containers

    tertiary = unset, // Used for accents or highlights
    onTertiary = unset, // For text/icons displayed on top of the tertiary color
    tertiaryContainer = Color(0xC21A3C60), // A tonal variation of the tertiary color for containers
    onTertiaryContainer = Color(0xFFFFFFFF), // Color for text/icons on tertiary containers

    background = Color(0xFA5A5E24), // Background color for screens and larger components
    onBackground = Color(0xFFFFFFFF), // Color for text/icons displayed on top of the background color

    surface = Color(0xFFE2E2E2), // For surfaces, cards, menus and tabs
    onSurface = Color(0xFF000000), // For text/icons displayed on top of the surface color
    surfaceVariant = Color(0xFF303030), // Another surface color variant for differentiation
    onSurfaceVariant = Color(0xFF242424), // For text/icons on surface variant
    surfaceTint = Color(0xFF303030), // Overlay color for elevated surfaces to create a tint effect

    inverseSurface = Color(0xFFE1E1E1), // Inverse of the surface color for contrasting UI elements. Used in ToolTip
    inverseOnSurface = Color.Black, // For text/icons on inverse surfaces. Used in ToolTip text

    error = Color(0xFFB00020), // Color used to indicate errors.
    onError = Color(0xFFFFA8A8), // Color for text/icons displayed on top of the error color.
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color(0xFF2E000B),

    outline = Color(0xFF313131), // For outlines/borders of OutlinedTextField and OutlinedButton and switches on disabled
    outlineVariant = Color(0xFF000000), // For HorizontalDivider

    scrim = Color(0xFF737373), // Applied on the background of unfocused content, when some popup/dialog is shown

    surfaceBright = unset,
    surfaceContainer = Color(0xFFC3C3C3), // Used in dropdown menus
    surfaceContainerHigh = Color(0xB9B6D1FF), // Used for AlertDialogs
    surfaceContainerHighest = Color(0xFF007CD7), //for card and normal switch
    surfaceContainerLow = unset,
    surfaceContainerLowest = unset,
    surfaceDim = unset,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SampleComposable(){
    Box {
        Column(
            Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("HorizontalDivider", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            HorizontalDivider()
            HorizontalDivider(thickness = 10.dp)

            var isVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = "Input",
                onValueChange = {},
                label = { Text("Label") },
                trailingIcon = { // Add the toggle button
                    val image = if (isVisible) MaterialIcons.VisibilityOff else MaterialIcons.Visibility
                    IconButton(onClick = { isVisible = !isVisible }) {
                        Icon(imageVector = image, "description")
                    }
                }
            )

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
            Switch(checked = false, onCheckedChange = { isChecked = !isChecked })

            var slider by remember { mutableFloatStateOf(0.5f) }
            Slider(value = slider, onValueChange = { slider = it })

            val animatedProgress = rememberInfiniteTransition("").animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = smooth(3000),
                    repeatMode = RepeatMode.Reverse
                )
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

            CenteredRow {
                Card(
                    Modifier.widthIn(0.dp, 30.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = roundMessageBox(true)
                ) { CircularProgressIndicator(Modifier.padding(10.dp)) }

                Card(
                    Modifier.widthIn(0.dp, 30.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = roundMessageBox(true)
                ) { CircularProgressIndicator(Modifier.padding(10.dp)) }

                Card(
                    Modifier.widthIn(0.dp, 30.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = roundMessageBox(true)
                ) { CircularProgressIndicator(Modifier.padding(10.dp)) }

                Box {
                    Box(
                        Modifier.size(33.dp, 62.dp)
                            .blur(6.dp, BlurredEdgeTreatment.Unbounded)
                            //.border(2.dp, Color.Black, RoundRectangleShape)
                    ) {
                        Box(
                            Modifier.size(33.dp, 63.dp)
                                .clip(RoundRectangleShape)
                                .background(Color(0xE1000000))
                        ) {

                        }
                    }

                    Box(
                        Modifier.size(30.dp, 60.dp)
                            .clip(RoundRectangleShape)
                            .background(Color.White)
                            //.border(1.dp, Color.Black, RoundRectangleShape)
                        ) { CircularProgressIndicator(Modifier.padding(10.dp)) }
                }

                Card(elevation = CardDefaults.outlinedCardElevation(defaultElevation = 5.dp)) { // ridiculous weak shadow...
                    Column(Modifier.padding(8.dp)) {
                        Text("Card Title", style = MaterialTheme.typography.titleMedium)
                        Text("Elevation", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Card(Modifier.shadow( // ridiculous weak shadow...
                    15.dp,
                    RoundRectangleShape,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                ).zIndex(1f)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Card", style = MaterialTheme.typography.titleMedium)
                        Text("Shadow", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Icon(
                Icons.Filled.Visibility,
                "Visibility",
                Modifier.size(50.dp),
                //MaterialTheme.colorScheme.secondary
            )

            DropdownOptions(
                label = R.string.model,
                preSelectedOption = 0,
                options = listOf("Model1", "Model2"),
                horizontalPadding = 8.dp,
                onNewOption = {}
            )

            var selectedTab by remember { mutableStateOf(Tab.YourAchievements) }

            Column(Modifier.fillMaxWidth()) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Tab.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTab.ordinal == index,
                            onClick = { selectedTab = tab },
                            text = { QuickText(tab.label, maxLines = 1) }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DarkColorSchemePreview() = PreviewComposable(enableDarkTheme = true){
    BoxWithBackground(R.drawable.background_dark_2) { SampleComposable() }
}

@Preview
@Composable
private fun DarkColorSchemePreview2() = PreviewComposable(enableDarkTheme = true){
    SampleComposable()
}

@Preview
@Composable
private fun LightColorSchemePreview() = PreviewComposable(enableDarkTheme = false){
    BoxWithBackground(R.drawable.background_light) { SampleComposable() }
}

@Preview
@Composable
private fun LightColorSchemePreview2() = PreviewComposable(enableDarkTheme = false) {
    SampleComposable()
}

/** Util to reduce repetitive code. Previews should be made on top of a Surface */
@Composable
fun PreviewComposable(
    enableDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) = AppTheme(enableDarkTheme) {
    Surface(Modifier.fillMaxSize().wrapContentSize()) { content() }
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
        enableDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}