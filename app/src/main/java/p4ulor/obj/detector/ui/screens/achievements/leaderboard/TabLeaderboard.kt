package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import p4ulor.obj.detector.R
import p4ulor.obj.detector.data.domains.firebase.ObjectDetectionStats
import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.data.utils.ConnectionStatus
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.animations.VerticallyAnimatedVisibility
import p4ulor.obj.detector.ui.components.IconMediumSize
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.MaterialIconsExt
import p4ulor.obj.detector.ui.components.QuickText
import p4ulor.obj.detector.ui.components.utils.BoxWithBackground
import p4ulor.obj.detector.ui.components.utils.CenteredColumn
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.GeneralPadding
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingMedium
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingSmall
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingTiny
import p4ulor.obj.detector.ui.components.utils.RoundRectangleShape
import p4ulor.obj.detector.ui.components.utils.toast
import p4ulor.obj.detector.ui.theme.PreviewComposable

private const val TOP_USERS_CAP = 5

@Composable
fun TabLeaderboard(
    currUser: User?,
    topUsers: List<User>,
    topObjects: List<ObjectDetectionStats>,
    connectionStatus: ConnectionStatus,
    onSignInWithGoogle: () -> Unit,
    onSignOut: () -> Unit,
    onSubmitAchievements: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val ctx = LocalContext.current
    
    VerticallyAnimatedVisibility(visible = currUser != null) {
        Column {
            Card(
                Modifier.fillMaxWidth().padding(GeneralPaddingSmall),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                CenteredRow(GeneralPadding.let { Modifier.padding(it) }) {
                    Column {
                        CenteredRow(Modifier) {
                            Text("${currUser?.name}", style = MaterialTheme.typography.headlineSmall)
                            ProfilePicture(currUser?.photoUri)
                        }
                        Text("${currUser?.points} ${stringResource(R.string.points)}")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    LeaderboardActions(
                        onSubmitAchievements = onSubmitAchievements,
                        onDeleteAccount = onDeleteAccount
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedButton(onClick = onSignOut) {
                            QuickText(R.string.logout)
                            with(MaterialIconsExt.Logout) {
                                Icon(this, name, Modifier.padding(horizontal = GeneralPaddingTiny))
                            }
                        }
                    }
                }
            }

            Card(
                Modifier.padding(GeneralPaddingSmall).fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
            ) {
                CenteredColumn(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    QuickText(
                        R.string.top_user_points,
                        Modifier.padding(GeneralPaddingSmall),
                        textStyle = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(GeneralPaddingSmall))

                    topUsers.take(TOP_USERS_CAP).forEachIndexed { index, user ->
                        Card(
                            Modifier.padding(horizontal = GeneralPaddingSmall, vertical = GeneralPaddingTiny),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            CenteredRow(Modifier.padding(GeneralPaddingSmall)) {
                                Text("${1 + index}")
                                ProfilePicture(user.photoUri)
                                Text(user.name)
                                Spacer(Modifier.weight(1f))
                                Text("${currUser?.points} ${stringResource(R.string.points)}")
                            }
                        }
                    }

                    Spacer(Modifier.height(GeneralPaddingMedium))

                    QuickText(
                        R.string.top_detected_objects,
                        textStyle = MaterialTheme.typography.headlineSmall
                    )

                    DonutChartWithLabels( // todo with topObjects
                        data = listOf(
                            Triple("Pink", 50f, Color(0xFF9800FF)),
                            Triple("2", 15f, Color(0xFF0AFF0D)),
                            Triple("3", 15f, Color(0xFF4CAF50)),
                            Triple("4", 20f, Color(0xAA0061FF))
                        ),
                        donutSize = 200.dp,
                        Modifier.padding(GeneralPadding)
                    )
                }
            }
        }
    }

    VerticallyAnimatedVisibility(visible = currUser == null) {
        CenteredColumn {
            SignInWithGoogle(Modifier.width(200.dp), onClick = {
                if (connectionStatus.isEnabled) {
                    i("onSignInWithGoogle")
                    onSignInWithGoogle()
                } else {
                    ctx.toast(R.string.no_internet_connection)
                }
            })
        }
    }
}

@Composable
private fun ProfilePicture(photoUri: String?) {
    val isValid = photoUri.orEmpty().isNotEmpty()
    AsyncImage(
        model = if(isValid) photoUri else null,
        contentDescription = "User profile picture",
        Modifier.size(IconMediumSize).clip(RoundRectangleShape).padding(1.dp),
        colorFilter = if(isValid) {
            null
        } else {
            ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
        },
        fallback = rememberVectorPainter(MaterialIcons.Person)
    )
}

@Preview
@Composable
private fun TabLeaderboardPreviewWithUser() = PreviewComposable(enableDarkTheme = true) {
    TabLeaderboard(
        currUser = User("Paulo", "123", "", 22.3f),
        topObjects = emptyList(),
        connectionStatus = ConnectionStatus.On,
        topUsers = buildList { repeat(8) { add(User("Paulo", "123", "", 22.3f)) } },
        onSignInWithGoogle = { },
        onSignOut = { },
        onSubmitAchievements = { },
        onDeleteAccount = { }
    )
}

@Preview
@Composable
private fun TabLeaderboardPreviewWithUserWithBackground() = PreviewComposable(enableDarkTheme = true) {
    BoxWithBackground(R.drawable.background_dark_2) {
        TabLeaderboard(
            currUser = User("Paulo", "123", "", 22.3f),
            topUsers = buildList { repeat(8) { add(User("Paulo", "123", "", 22.3f)) } },
            topObjects = emptyList(),
            connectionStatus = ConnectionStatus.On,
            onSignInWithGoogle = { },
            onSignOut = { },
            onSubmitAchievements = { },
            onDeleteAccount = { }
        )
    }
}

@Preview
@Composable
private fun TabLeaderboardPreviewNoUser() = PreviewComposable(enableDarkTheme = true) {
    TabLeaderboard(
        currUser = null,
        connectionStatus = ConnectionStatus.Off,
        topUsers = emptyList(),
        topObjects = emptyList(),
        onSignInWithGoogle = { },
        onSignOut = { },
        onSubmitAchievements = { },
        onDeleteAccount = { }
    )
}
