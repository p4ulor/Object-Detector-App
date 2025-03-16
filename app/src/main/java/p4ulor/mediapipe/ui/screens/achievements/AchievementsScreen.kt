package p4ulor.mediapipe.ui.screens.achievements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import p4ulor.mediapipe.android.viewmodels.AchievementsViewModel
import p4ulor.mediapipe.data.domains.mediapipe.Achievement
import p4ulor.mediapipe.data.domains.mediapipe.UserAchievements
import p4ulor.mediapipe.data.utils.getTodaysDate
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.theme.PreviewComposable

private val GeneralPadding = 12.dp

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
        AchievementsScreenUi(
            userAchievements = userAchievements ?: UserAchievements()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreenUi(userAchievements: UserAchievements) {

    var selectedTab by remember { mutableStateOf(Tab.YourAchievements) }
    val listState = rememberLazyListState()

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        PrimaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent,
        ) {
            Tab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.ordinal == index,
                    text = { QuickText(tab.label, maxLines = 1) },
                    onClick = { selectedTab = tab },
                )
            }
        }

        Spacer(Modifier.size(GeneralPadding * 2))

        LazyColumn(
            Modifier.weight(1f), // weight is necessary for reverseLayout to work. Makes so the LazyColumn to expand and align itself with the bottom edge of the parent Column
            state = listState,
        ) {
            // The key is required for animateItem to work as it's docs say
            items(userAchievements.achievements, key = { it.objectName }) { message ->
                Text(message.objectName)
            }
        }
    }
}

@Preview
@Composable
private fun AchievementsScreenUiPreview() = PreviewComposable {
    AchievementsScreenUi(
        UserAchievements(
            achievements = listOf(
                Achievement("car", getTodaysDate()),
                Achievement("cat", getTodaysDate()),
                Achievement("bench")
            )
        )
    )
}
