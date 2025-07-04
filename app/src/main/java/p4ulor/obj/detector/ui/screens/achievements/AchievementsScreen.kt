package p4ulor.obj.detector.ui.screens.achievements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import p4ulor.obj.detector.R
import p4ulor.obj.detector.android.viewmodels.AchievementsViewModel
import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.utils.ConnectionStatus
import p4ulor.obj.detector.data.utils.getTodaysDate
import p4ulor.obj.detector.ui.animations.smooth
import p4ulor.obj.detector.ui.components.EzText
import p4ulor.obj.detector.ui.components.utils.toast
import p4ulor.obj.detector.ui.screens.achievements.leaderboard.TabLeaderboard
import p4ulor.obj.detector.ui.screens.achievements.local.OrderOption
import p4ulor.obj.detector.ui.screens.achievements.local.TabYourAchievements
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun AchievementsScreen(){
    val ctx = LocalContext.current
    val vm = koinViewModel<AchievementsViewModel>()

    val selectedTab by vm.selectedTab.collectAsState()

    val yourAchievements by vm.yourAchievements.collectAsState()
    val leaderboard by vm.leaderboard.collectAsState()
    var isLoaded by rememberSaveable { mutableStateOf(false) } // ensures there's always an animation even if are already loaded

    val yourAchievementsCallbacks = remember {
        YourAchievementsCallbacks(
            onDeleteAchievements = { vm.deleteAchievements() },
            onChangeOrderOption = { vm.setOrderOption(it) }
        )
    }

    val leaderboardCallbacks = remember {
        LeaderboardSCallbacks(
            onSignInWithGoogle = { vm.signInWithGoogle() },
            onSignOut = { vm.signOut() },
            onSubmitAchievements = {
                vm.submitAchievements(onNoNewAchievements = {
                    ctx.toast(R.string.no_new_achievements)
                })
            },
            onDeleteAccount = { vm.deleteAccount() },
            onRefreshLeaderboard = { vm.refreshLeaderboard() }
        )
    }

    LaunchedEffect(Unit) {
        vm.loadAchievements(onLoaded = {
            isLoaded = true
        })
    }

    LaunchedEffect(leaderboard.connectionStatus) {
        if(leaderboard.connectionStatus.isDisconnected) {
            ctx.toast(R.string.no_internet_connection)
        }
    }

    AnimatedVisibility(
        visible = isLoaded,
        enter = fadeIn(smooth()) + scaleIn(),
        exit = ExitTransition.None // improves performance when navigating
    ) {
        AchievementsScreenUi(
            selectedTab = selectedTab,
            onSelectedTabChanged = { vm.setSelectedTab(it) },
            yourAchievementsState = yourAchievements,
            yourAchievementsCallbacks = yourAchievementsCallbacks,
            leaderboardState = leaderboard,
            leaderboardSCallbacks = leaderboardCallbacks,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreenUi(
    selectedTab: Tab,
    onSelectedTabChanged: (Tab) -> Unit,
    yourAchievementsState: YourAchievementsState,
    yourAchievementsCallbacks: YourAchievementsCallbacks,
    leaderboardState: LeaderboardState,
    leaderboardSCallbacks: LeaderboardSCallbacks
) {

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent
        ) {
            Tab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.ordinal == index,
                    text = { EzText(tab.label, maxLines = 1) },
                    onClick = { onSelectedTabChanged(tab) },
                )
            }
        }

        when(selectedTab){
            Tab.YourAchievements -> {
                TabYourAchievements(
                    achievements = yourAchievementsState.achievements,
                    orderOptions = yourAchievementsState.orderOptions,
                    onDeleteAchievements = yourAchievementsCallbacks.onDeleteAchievements,
                    onChangeOrderOption = yourAchievementsCallbacks.onChangeOrderOption
                )
            }
            Tab.Leaderboard -> {
                TabLeaderboard(
                    currUser = leaderboardState.currUser,
                    topUsers = leaderboardState.topUsers,
                    topObjects = leaderboardState.topObjects,
                    connectionStatus = leaderboardState.connectionStatus,
                    localAchievements = yourAchievementsState.achievements, // to calculate the diff between local and submitted achievements
                    onSignInWithGoogle = leaderboardSCallbacks.onSignInWithGoogle,
                    onSignOut = leaderboardSCallbacks.onSignOut,
                    onSubmitAchievements = leaderboardSCallbacks.onSubmitAchievements,
                    onDeleteAccount = leaderboardSCallbacks.onDeleteAccount,
                    onRefreshLeaderboard = leaderboardSCallbacks.onRefreshLeaderboard
                )
            }
        }
    }
}

@Preview
@Composable
private fun AchievementsScreenUiPreview() = PreviewComposable(enableDarkTheme = true) {
    val achievements = buildList {
        add(Achievement("START", 0f))
        repeat(20) {
            addAll(
                listOf(
                    Achievement("car$it", 1f, getTodaysDate()),
                    Achievement("cat$it", 0.5f, getTodaysDate()),
                    Achievement("bench$it", 0f)
                )
            )
        }
        add(Achievement("END", 0f))
    }

    val showAchievements = true

    AchievementsScreenUi(
        if (showAchievements) Tab.YourAchievements else Tab.Leaderboard,
        onSelectedTabChanged = {},
        yourAchievementsState = YourAchievementsState(
            achievements = achievements,
            orderOptions = OrderOption.Name
        ),
        yourAchievementsCallbacks = YourAchievementsCallbacks(
            onDeleteAchievements = {  },
            onChangeOrderOption = {  }
        ),
        leaderboardState = LeaderboardState(
            currUser = User(),
            topUsers = emptyList(),
            topObjects = emptyList(),
            connectionStatus = ConnectionStatus.Off
        ),
        leaderboardSCallbacks = LeaderboardSCallbacks(
            onSignInWithGoogle = { },
            onSignOut = { },
            onSubmitAchievements = { },
            onDeleteAccount = { },
            onRefreshLeaderboard = { }
        )
    )
}
