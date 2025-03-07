package p4ulor.mediapipe.ui.screens.achievements

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import p4ulor.mediapipe.android.viewmodels.AchievementsViewModel
import p4ulor.mediapipe.ui.components.QuickText
import p4ulor.mediapipe.ui.components.utils.CenteredContent
import p4ulor.mediapipe.ui.theme.PreviewComposable

@Composable
fun AchievementsScreen(){
    val vm = koinViewModel<AchievementsViewModel>()

    AchievementsScreenUi()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreenUi(){

    var selectedTab by remember { mutableStateOf(Tab.YourAchievements) }

    Column {
        PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
            Tab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.ordinal == index,
                    onClick = { selectedTab = tab },
                    text = { QuickText(tab.label, maxLines = 1) }
                )
            }
        }
    }

    CenteredContent {
        Text("Achievements")
    }
}

@Preview
@Composable
private fun AchievementsScreenUiPreview() = PreviewComposable {
    AchievementsScreenUi()
}