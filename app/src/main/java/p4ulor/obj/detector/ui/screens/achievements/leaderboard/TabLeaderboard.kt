package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import p4ulor.obj.detector.R
import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.components.IconSmallSize
import p4ulor.obj.detector.ui.components.MaterialIconsExt
import p4ulor.obj.detector.ui.components.QuickIcon
import p4ulor.obj.detector.ui.components.QuickText
import p4ulor.obj.detector.ui.components.utils.CenteredColumn
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.GeneralPadding
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingSmall
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun TabLeaderboard(
    onSignInWithGoogle: () -> Unit,
    onLogOut: () -> Unit,
    currUser: User?
) {
    AnimatedVisibility(visible = currUser != null) {
        Card(
            Modifier.padding(GeneralPaddingSmall),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            CenteredRow {
                Column(Modifier.padding(GeneralPadding)) {
                    Text("${currUser?.name}", style = MaterialTheme.typography.headlineSmall)
                    Text("${currUser?.points} points")
                }
                Spacer(modifier = Modifier.weight(1f))
                Row (
                    Modifier.clickable { onLogOut() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    QuickText(R.string.logout)
                    QuickIcon(MaterialIconsExt.Logout, IconSmallSize) { }
                }
            }
        }

    }

    AnimatedVisibility(visible = currUser == null) {
        CenteredColumn {
            SignInWithGoogle(Modifier.width(200.dp), onClick = {
                i("SignInWithGoogle")
                onSignInWithGoogle()
            })
        }
    }
}

@Preview
@Composable
private fun TabLeaderboardPreviewWithUser() = PreviewComposable(enableDarkTheme = true) {
    TabLeaderboard(
        onSignInWithGoogle = {},
        onLogOut = {},
        currUser = User("Paulo", "123", 22.3f, 10)
    )
}

@Preview
@Composable
private fun TabLeaderboardPreviewNoUser() = PreviewComposable(enableDarkTheme = true) {
    TabLeaderboard(
        onSignInWithGoogle = {},
        onLogOut = {},
        currUser = null
    )
}
