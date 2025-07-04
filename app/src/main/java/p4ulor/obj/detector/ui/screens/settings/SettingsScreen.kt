package p4ulor.obj.detector.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import org.koin.androidx.compose.koinViewModel
import p4ulor.obj.detector.R
import p4ulor.obj.detector.android.viewmodels.SettingsViewModel
import p4ulor.obj.detector.data.sources.local.preferences.UserPreferences
import p4ulor.obj.detector.data.sources.local.preferences.UserSecretPreferences
import p4ulor.obj.detector.ui.animations.smooth
import p4ulor.obj.detector.ui.components.EzText
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.GeneralPadding
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingSmall
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingTiny
import p4ulor.obj.detector.ui.components.utils.UiTestTag
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun SettingsScreen() {
    val vm = koinViewModel<SettingsViewModel>()
    var currentPrefs by remember { mutableStateOf(UserPreferences()) } // Initial value of null is not used to avoid using !!
    var currentSecretPrefs by remember { mutableStateOf(UserSecretPreferences()) }
    val hasConnection by vm.network.hasConnection.collectAsState(initial = false) // Is cancelled when leaving composition
    var isLoaded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) { // Load only on first composition rendering
        currentPrefs = vm.getUserPrefs().await()
        currentSecretPrefs = vm.getUserSecretPrefs().await()
        isLoaded = true
    }

    AnimatedVisibility(
        visible = isLoaded,
        enter = fadeIn(smooth()) + scaleIn(),
        exit = ExitTransition.None // improves performance when navigating
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
    Column(
        Modifier.padding(GeneralPadding).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

    CenteredRow {
        EzText(R.string.connectivity_status)
        LottieAnimation(composition, modifier)
    }
}

@Composable
private fun SavePicturesCheckBox(currPrefs: UserPreferences, onNewPrefs: (UserPreferences) -> Unit) {
    var savePictures by remember { mutableStateOf(currPrefs.savePictures) }

    CenteredRow (horizontalPadding = GeneralPaddingTiny) {
        EzText(R.string.save_pictures)

        Checkbox(
            checked = savePictures,
            onCheckedChange = {
                savePictures = !savePictures
                onNewPrefs(currPrefs.apply {
                    this.savePictures = it
                })
            },
            Modifier.testTag(UiTestTag.settingsCheckBox),
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