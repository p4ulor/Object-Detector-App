package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import p4ulor.obj.detector.R
import p4ulor.obj.detector.data.domains.firebase.ObjectDetectionStats
import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.data.domains.firebase.UserAchievement
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.utils.ConnectionStatus
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.animations.VerticallyAnimatedVisibility
import p4ulor.obj.detector.ui.animations.smooth
import p4ulor.obj.detector.ui.components.Icon
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.MaterialIconsExt
import p4ulor.obj.detector.ui.components.QuickAlertDialog
import p4ulor.obj.detector.ui.components.QuickText
import p4ulor.obj.detector.ui.components.utils.BoxWithBackground
import p4ulor.obj.detector.ui.components.utils.CenteredColumn
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.GeneralPadding
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingSmall
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingTiny
import p4ulor.obj.detector.ui.components.utils.rememberToggleableState
import p4ulor.obj.detector.ui.components.utils.toast
import p4ulor.obj.detector.ui.theme.PreviewComposable

private const val TOP_USERS_CAP = 5

@Composable
fun TabLeaderboard(
    currUser: User?,
    topUsers: List<User>,
    topObjects: List<ObjectDetectionStats>,
    connectionStatus: ConnectionStatus,
    localAchievements: List<Achievement>,
    onSignInWithGoogle: () -> Unit = {},
    onSignOut: () -> Unit= {},
    onSubmitAchievements: () -> Unit= {},
    onDeleteAccount: () -> Unit= {},
    onRefreshLeaderboard: () -> Unit = {},
) {
    val ctx = LocalContext.current
    var showDeletionConfirmation = rememberToggleableState(false)

    val blurRadius: Dp by animateDpAsState(
        targetValue = if (showDeletionConfirmation.value) 20.dp else 0.dp,
        animationSpec = smooth(),
    )

    VerticallyAnimatedVisibility(visible = currUser != null) { // Login granted
        Column(Modifier.blur(blurRadius)) {
            Card(
                Modifier.fillMaxWidth().padding(GeneralPaddingSmall),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                CenteredRow(Modifier.padding(GeneralPadding)) {
                    Column(Modifier.widthIn(max = 150.dp)) {
                        Text( // Autosize text could have been used here, but I dont wanna risk changing the compose version so it messes up my project at this stage https://android-developers.googleblog.com/2025/05/androidify-building-delightful-ui-with-compose.html
                            "${currUser?.name}",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text("${currUser?.points} ${stringResource(R.string.points)}")
                    }

                    Spacer(Modifier.weight(1f))

                    LeaderboardMainActions(
                        localAchievements,
                        currUser?.achievements,
                        onSubmitAchievements
                    )

                    Spacer(Modifier.weight(1f))

                    UserProfileDropdown(
                        currUser?.photoUri,
                        dropDownActions = listOf(
                            DropdownAction(Icon.Material(MaterialIconsExt.Logout), action = { onSignOut() }),
                            DropdownAction(Icon.Material(MaterialIcons.PersonOff), action = { showDeletionConfirmation.toggle() })
                        )
                    )
                }
            }

            TopDataDashboard(topUsers, topObjects, onRefreshLeaderboard)
        }

        if (showDeletionConfirmation.value) {
            DeletionConfirmationDialog(
                confirmClick = {
                    showDeletionConfirmation.toggle()
                    onDeleteAccount()
                },
                dismissClick = { showDeletionConfirmation.toggle() }
            )
        }
    }

    VerticallyAnimatedVisibility(visible = currUser == null) { // Login not granted
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopDataDashboard(
    topUsers: List<User>,
    topObjects: List<ObjectDetectionStats>,
    onRefreshLeaderboard: () -> Unit
) {
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    var currTopUsers by remember { mutableStateOf(topUsers) }
    
    LaunchedEffect(topUsers.hashCode()) {
        currTopUsers = topUsers
    }

    LaunchedEffect(isRefreshing) {
        delay(1000) // its easier, than handling the case where the lists dont change and their hash is the same...
        isRefreshing = false
    }

    Card(
        Modifier.padding(GeneralPaddingSmall).fillMaxSize(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                onRefreshLeaderboard()
            }
        ) {
            CenteredColumn(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                QuickText(
                    R.string.top_user_points,
                    Modifier.padding(GeneralPadding),
                    textStyle = MaterialTheme.typography.titleLarge
                )

                currTopUsers.take(TOP_USERS_CAP).sortedByDescending { it.points }.forEachIndexed { index, user ->
                    Card(
                        Modifier.padding(horizontal = GeneralPaddingSmall, vertical = GeneralPaddingTiny),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        CenteredRow(Modifier.padding(GeneralPaddingSmall)) {
                            Text("${1 + index}", Modifier.padding(start = GeneralPaddingSmall))
                            UserProfilePicture(user.photoUri)
                            Text(user.name)
                            Spacer(Modifier.weight(1f))
                            Text("${user.points} ${stringResource(R.string.points)}")
                        }
                    }
                }

                QuickText(
                    R.string.top_detected_objects,
                    Modifier.padding(GeneralPadding),
                    textStyle = MaterialTheme.typography.titleLarge
                )

                DonutChartWithLabels(
                    data = topObjects.map {
                        Triple(it.objectName, it.detectionCount.toFloat(), null)
                    }.toList(),
                    Modifier.padding(GeneralPaddingSmall)
                )
            }
        }
    }
}

@Preview
@Composable
private fun TabLeaderboardPreviewWithUser() = PreviewComposable(enableDarkTheme = true) {

    var topUsers by remember {
        mutableStateOf(
            buildList { repeat(6) { add(User("Paulo", "uri", 22.3f)) } }
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            topUsers = buildList {
                repeat(4) {
                    add(User("Paulo", "uri", 22.3f))
                }
                add(User("Paulo", "uri", 4200.3f))
            }

            delay(3000)

            topUsers = buildList {
                add(User("Paulo", "uri", 220.3f))
                repeat(4) {
                    add(User("Paulo", "uri", 22.3f))
                }
            }
        }
    }

    TabLeaderboard(
        currUser = User("Paulo aaaaaaaaaaaaa", "uri", 22.3f),
        topObjects = emptyList(),
        connectionStatus = ConnectionStatus.On,
        topUsers = topUsers,
        localAchievements = listOf(
            Achievement("cat", 0.8f),
            Achievement("car", 0.3f)
        )
    )
}

@Preview
@Composable
private fun TabLeaderboardPreviewWithUserWithBackground() = PreviewComposable(enableDarkTheme = true) {
    val photoUri = remember { "https://us1.discourse-cdn.com/flex019/uploads/kotlinlang/original/2X/2/224964e73572d20c3aa9d68b4c14ae5d11749202.png" }
    BoxWithBackground(R.drawable.background_dark_2) {
        TabLeaderboard(
            currUser = User("Paulo aaaaaaaaaaaaa", photoUri, 22.3f),
            topUsers = buildList { repeat(8) { add(User("Paulo", photoUri, 22.3f)) } },
            topObjects = emptyList(),
            connectionStatus = ConnectionStatus.On,
            localAchievements = emptyList()
        )
    }
}

/** Will only display on real device, not in AS compose preview */
@Preview
@Composable
private fun TabLeaderboardPreviewNoUser() = PreviewComposable(enableDarkTheme = true) {
    TabLeaderboard(
        currUser = null,
        connectionStatus = ConnectionStatus.Off,
        topUsers = emptyList(),
        topObjects = emptyList(),
        localAchievements = emptyList()
    )
}
