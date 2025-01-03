package p4ulor.mediapipe.ui.screens.root

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon as AndroidIcon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.viewmodels.MainViewModel
import p4ulor.mediapipe.android.utils.MainViewModelFactory
import p4ulor.mediapipe.i
import p4ulor.mediapipe.ui.components.Icon
import p4ulor.mediapipe.ui.components.AppIcons
import p4ulor.mediapipe.ui.components.BoxWithBackground
import p4ulor.mediapipe.ui.components.IconDefaultSize
import p4ulor.mediapipe.ui.screens.home.HomeScreen
import p4ulor.mediapipe.ui.theme.AppTheme
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.SmoothHorizontalDivider
import p4ulor.mediapipe.ui.screens.about.AboutScreen
import p4ulor.mediapipe.ui.screens.settings.SettingsScreen
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun RootScreen() = AppTheme {
    var currentDestination by remember { mutableStateOf<NavItem?>(null) }
    val navController = rememberNavController()

    BoxWithBackground(R.drawable.background_default) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                SmoothHorizontalDivider()
                NavigationBar (Modifier.height(65.dp), containerColor = Color.Transparent) {
                    bottomBarDestinations.forEach { item ->
                        buildNavigationBarItem(item, currentDestination, this, onClick = {
                            currentDestination = it
                            navController.navigate(item.title)
                        })
                    }
                }
            },
            content = { padding ->

                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(LocalContext.current)
                )

                NavHost(
                    modifier = Modifier.padding(padding),
                    navController = navController,
                    startDestination = Screens.Home.name,
                ) {
                    composable(route = Screens.About.name) { AboutScreen() }
                    composable(route = Screens.Home.name) { HomeScreen(viewModel) }
                    composable(route = Screens.Settings.name) { SettingsScreen() }
                }
            },
            floatingActionButton = {
                var offsetX by remember { mutableFloatStateOf(0f) }
                var offsetY by remember { mutableFloatStateOf(0f) }
                val screenWidthPx = with(LocalDensity.current) {
                    LocalConfiguration.current.screenWidthDp.dp.toPx()
                }
                val screenHeightPx = with(LocalDensity.current) {
                    LocalConfiguration.current.screenHeightDp.dp.toPx()
                }
                FloatingActionButton(
                    onClick = { /* Take pic */ },
                    modifier = Modifier.padding(bottom = 14.dp)
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                //change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y

                                // Keep the FAB within screen bounds
                                /*val screenWidth = this@BoxWithBackground.maxWidth.value
                                val screenHeight = this@BoxWithBackground.maxHeight.value
                                i("!!! ${offsetX.absoluteValue} & $offsetY $screenWidthPx and $screenHeightPx")

                                offsetX = offsetX.absoluteValue.coerceIn(0f, screenWidth)
                                offsetY = offsetY.absoluteValue.coerceIn(0f, screenHeight)*/
                            }
                        },
                    containerColor = Color(0xB76D6D6D)
                ) {
                    Icon(AppIcons.Camera){}
                }
            }
        )
    }
}

@Composable
private fun buildNavigationBarItem(
    item: NavItem,
    currentDestination: NavItem?,
    rowScope: RowScope,
    onClick: (item: NavItem) -> Unit,
) = with(rowScope) {
    NavigationBarItem(
        selected = currentDestination == item,
        onClick = { onClick(item) },
        label = { Text(item.title) },
        alwaysShowLabel = false, // the label will only be shown when this item is selected
        icon = {
            BadgedBox(
                badge = {
                    if(item.hasNews) {
                        Badge()
                    }
                }
            ) {
                AndroidIcon(
                    imageVector = item.selectedIcon,
                    contentDescription = item.title,
                    modifier = Modifier.size(item.size)
                )
            }
        }
    )
}

/** This enum defines the order in which the destinations appear */
enum class Screens(
    val icon: AppIcons? = null,
    val materialIcons: ImageVector? = null,
    val size: Dp = 25.dp
) {
    About(materialIcons = MaterialIcons.Info, size = 22.dp),
    Home(materialIcons = MaterialIcons.Home),
    Settings(icon = AppIcons.Settings)
}

/** If it's failing, comment out uses of [LocalContext.current]. Find a solution for this */
@Preview
@Composable
fun RootScreenPreview(){
    RootScreen()
}