package p4ulor.obj.detector.ui.screens.achievements

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import p4ulor.obj.detector.ui.components.utils.CenteredContent
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun TabLeaderboard() {
    CenteredContent {
        SignInWithGoogle(Modifier.size(200.dp), onClick = {

        })
    }
}

@Preview
@Composable
private fun TabLeaderboardPreview() = PreviewComposable(enableDarkTheme = true) {
    TabLeaderboard()
}
