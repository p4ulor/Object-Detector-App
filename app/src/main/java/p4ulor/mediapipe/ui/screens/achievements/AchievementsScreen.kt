package p4ulor.mediapipe.ui.screens.achievements

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import p4ulor.mediapipe.android.viewmodels.AchievementsViewModel
import p4ulor.mediapipe.data.domains.mediapipe.Achievement
import p4ulor.mediapipe.data.utils.getTodaysDate
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.theme.PreviewComposable

@Composable
fun AchievementsScreen(){
    val vm = koinViewModel<AchievementsViewModel>()

    val userAchievements by vm.userAchievements.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.loadAchievements()
    }

    AnimatedVisibility(
        visible = userAchievements != null,
        enter = fadeIn(smooth()) + scaleIn()
    ) {
        userAchievements?.let {
            AchievementsScreenUi(
                achievements = it.achievements,
                onDeleteAchievements = { vm.deleteAchievements() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreenUi(achievements: List<Achievement>, onDeleteAchievements: () -> Unit) {
    var selectedTab by remember { mutableStateOf(Tab.YourAchievements) }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent, // So the tabs rectangles don't use the default material theme, and just show the background
        ) {
            Tab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.ordinal == index,
                    text = { QuickText(tab.label, maxLines = 1) },
                    onClick = { selectedTab = tab },
                )
            }
        }

        when(selectedTab){
            Tab.YourAchievements -> {
                TabYourAchievements(achievements, onDeleteAchievements)
            }
            else -> TabLeaderboard()
        }
    }
}

@Preview
@Composable
private fun AchievementsScreenUiPreview() = PreviewComposable(enableDarkTheme = true) {
    val achievements = buildList {
        add(Achievement("START"))
        repeat(20) {
            addAll(
                listOf(
                    Achievement("car$it", getTodaysDate()),
                    Achievement("cat$it", getTodaysDate()),
                    Achievement("bench$it")
                )
            )
        }
        add(Achievement("END"))
    }

    AchievementsScreenUi(achievements, onDeleteAchievements = {})
}
