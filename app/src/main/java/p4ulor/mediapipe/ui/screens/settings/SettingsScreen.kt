package p4ulor.mediapipe.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.flow.first
import org.koin.androidx.compose.koinViewModel
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.viewmodels.SettingsViewModel
import p4ulor.mediapipe.data.sources.local.preferences.UserPreferences
import p4ulor.mediapipe.data.sources.local.preferences.UserSecretPreferences
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.utils.GeneralPadding
import p4ulor.mediapipe.ui.components.utils.HorizontalPadding
import p4ulor.mediapipe.ui.theme.PreviewComposable

@Composable
fun SettingsScreen() {
    val vm = koinViewModel<SettingsViewModel>()
    var currentPrefs by remember { mutableStateOf(UserPreferences()) } // Initial value of null is not used to avoid using !!
    var currentSecretPrefs by remember { mutableStateOf(UserSecretPreferences()) }
    val hasConnection by vm.network.hasConnection.collectAsState(initial = false) // Is cancelled when leaving composition
    var isLoaded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) { // Collect only on first composition rendering
        currentPrefs = vm.getUserPrefs().first() // These need to be loaded everytime
        currentSecretPrefs = vm.getUserSecretPrefs().first()
        isLoaded = true
    }

    AnimatedVisibility(
        visible = isLoaded,
        enter = fadeIn(smooth()) + scaleIn()
    ) {
        SettingsScreenUi(
            hasConnection,
            currentPrefs,
            currentSecretPrefs,
            onSaveUserPrefs = {
                vm.saveUserPrefs(it)
            },
            onSaveUserSecretPrefs = {
                vm.saveUserSecretPrefs(it)
            }
        )
    }
}

@Composable
private fun SettingsScreenUi(
    hasConnection: Boolean,
    userPreferences: UserPreferences,
    userSecretPreferences: UserSecretPreferences,
    onSaveUserPrefs: (UserPreferences) -> Unit,
    onSaveUserSecretPrefs: (UserSecretPreferences) -> Unit
) {
    Column(Modifier.padding(GeneralPadding), horizontalAlignment = Alignment.CenterHorizontally) {
        MediaPipeSettings(
            currPrefs = userPreferences,
            onNewPrefs = { onSaveUserPrefs(it) }
        )

        Spacer(Modifier.size(GeneralPadding * 2))

        GeminiSettings(
            currPrefs = userSecretPreferences,
            onNewPrefs = { onSaveUserSecretPrefs(it) }
        )

        Spacer(Modifier.size(GeneralPadding * 2))

        ConnectivityStatus(hasConnection)

        Spacer(Modifier.size(GeneralPadding * 2))

        SavePicturesCheckBox(
            currPrefs = userPreferences,
            onNewPrefs = { onSaveUserPrefs(it) }
        )
    }
}



@Composable
private fun ConnectivityStatus(hasConnection: Boolean) {
    val (animation, modifier) = if (hasConnection) { // Because the lottie files have different paddings...
        R.raw.success_animation to Modifier
            .size(30.dp, 30.dp)
            .scale(3f)
            .padding(horizontal = 3.dp)
    } else {
        R.raw.error_animation to Modifier
            .size(30.dp, 30.dp)
            .padding(horizontal = 2.dp)
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        QuickText(R.string.connectivity_status)
        LottieAnimation(composition, modifier)
    }
}

@Composable
private fun SavePicturesCheckBox(currPrefs: UserPreferences, onNewPrefs: (UserPreferences) -> Unit) {
    var savePictures by remember { mutableStateOf(currPrefs.savePictures) }

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        QuickText(R.string.save_pictures)
        WidthSpacer(HorizontalPadding)
        Checkbox(
            checked = savePictures,
            onCheckedChange = {
                savePictures = !savePictures
                onNewPrefs(currPrefs.apply {
                    this.savePictures = it
                })
            },
            colors = CheckboxDefaults.colors(
                uncheckedColor = MaterialTheme.colorScheme.scrim
            )
        )
    }
}

@Composable
fun ColumnScope.SettingsHeader(styledText: AnnotatedString){
    Text(
        styledText,
        Modifier
            .padding(bottom = GeneralPadding)
            .align(Alignment.Start),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
fun WidthSpacer(width: Dp) = Spacer(Modifier.width(width))

@Preview
@Composable
private fun SettingsScreenPreview() = PreviewComposable {
    SettingsScreenUi(
        hasConnection = false,
        UserPreferences(),
        UserSecretPreferences(),
        {},
        {}
    )
}