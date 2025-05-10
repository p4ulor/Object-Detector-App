package p4ulor.obj.detector.ui.screens.achievements

import androidx.compose.animation.AnimatedVisibility
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
import p4ulor.obj.detector.data.domains.firebase.ObjectDetectionStats
import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.utils.ConnectionStatus
import p4ulor.obj.detector.data.utils.getTodaysDate
import p4ulor.obj.detector.ui.animations.smooth
import p4ulor.obj.detector.ui.components.QuickText
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

    // Your Achievements
    val userAchievements by vm.userAchievements.collectAsState()
    val orderOption by vm.orderOption.collectAsState()
    var isLoaded by rememberSaveable { mutableStateOf(false) } // ensures there's always an animation even if are already loaded

    // Leaderboard
    val currUser by vm.currUser.collectAsState()
    val connectionStatus by vm.connectionStatus.collectAsState(initial = ConnectionStatus.Off)

    LaunchedEffect(Unit) {
        vm.loadAchievements()
        isLoaded = true
    }

    LaunchedEffect(connectionStatus) {
        if(connectionStatus.isDisconnected) {
            ctx.toast(R.string.no_internet_connection)
        }
    }

    AnimatedVisibility(
        visible = isLoaded && userAchievements.isNotEmpty(),
        enter = fadeIn(smooth()) + scaleIn()
    ) {
        userAchievements?.let {
            AchievementsScreenUi(
                selectedTab = selectedTab,
                onSelectedTabChanged = { vm.setSelectedTab(it) },
                achievements = it,
                orderOptions = orderOption,
                onChangeOrderOption = { vm.setOrderOption(it) },
                onDeleteAchievements = { vm.deleteAchievements() },
                onSignInWithGoogle = { vm.signInWithGoogle() },
                onLogOut = { vm.signOut() },
                currUser = currUser,
                connectionStatus = connectionStatus
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreenUi( // todo, find fix for these params...
    selectedTab: Tab,
    onSelectedTabChanged: (Tab) -> Unit,
    achievements: List<Achievement>,
    orderOptions: OrderOption,
    onChangeOrderOption: (OrderOption) -> Unit,
    onDeleteAchievements: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    onLogOut: () -> Unit,
    currUser: User? = null,
    topUsers: List<User> = emptyList(),
    topObjects: List<ObjectDetectionStats> = emptyList(),
    connectionStatus: ConnectionStatus
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
                    text = { QuickText(tab.label, maxLines = 1) },
                    onClick = { onSelectedTabChanged(tab) },
                )
            }
        }

        when(selectedTab){
            Tab.YourAchievements -> {
                TabYourAchievements(
                    achievements,
                    orderOptions,
                    onDeleteAchievements,
                    onChangeOrderOption
                )
            }
            Tab.Leaderboard -> {
                TabLeaderboard(
                    onSignInWithGoogle,
                    onLogOut,
                    currUser,
                    topUsers,
                    topObjects,
                    connectionStatus
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

    AchievementsScreenUi(
        Tab.YourAchievements,
        onSelectedTabChanged = {},
        achievements,
        OrderOption.Name,
        onChangeOrderOption = {},
        onDeleteAchievements = {},
        onSignInWithGoogle = {},
        onLogOut = {},
        connectionStatus = ConnectionStatus.Off
    )
}
