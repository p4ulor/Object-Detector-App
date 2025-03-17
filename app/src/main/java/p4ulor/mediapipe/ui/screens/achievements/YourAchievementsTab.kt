package p4ulor.mediapipe.ui.screens.achievements

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import p4ulor.mediapipe.data.domains.mediapipe.UserAchievements
import p4ulor.mediapipe.ui.components.utils.TransparencyGradient
import p4ulor.mediapipe.ui.components.utils.TransparentGradientPosition
import p4ulor.mediapipe.ui.components.utils.fadingEdge

@Composable
fun YourAchievementsTab(userAchievements: UserAchievements) {
    val listState = rememberLazyListState()

    var isFirstItemNotVisible by remember { mutableStateOf(false) }
    var isLastItemNotVisible by remember { mutableStateOf(false) }

    LaunchedEffect(listState.isScrollInProgress) {
        isFirstItemNotVisible = listState.layoutInfo
            .visibleItemsInfo
            .any { it.index == 0 }.not()
        isLastItemNotVisible = listState.layoutInfo
            .visibleItemsInfo
            .any { it.index + 1 == userAchievements.achievements.size }.not()
    }

    LazyColumn(
        Modifier.fillMaxSize().fadingEdge(
            TransparencyGradient(
                position = when {
                    isFirstItemNotVisible && isLastItemNotVisible -> TransparentGradientPosition.Vertical
                    isFirstItemNotVisible -> TransparentGradientPosition.Top
                    isLastItemNotVisible -> TransparentGradientPosition.Bottom
                    else -> TransparentGradientPosition.None
                }
            )
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = listState,
    ) {
        items(userAchievements.achievements, key = { it.objectName }) { message ->
            Text(message.objectName.replaceFirstChar { it.uppercase() })
        }
    }
}