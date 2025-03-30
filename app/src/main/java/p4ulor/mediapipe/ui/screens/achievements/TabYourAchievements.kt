package p4ulor.mediapipe.ui.screens.achievements

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import p4ulor.mediapipe.R
import p4ulor.mediapipe.data.domains.mediapipe.Achievement
import p4ulor.mediapipe.data.utils.capitalized
import p4ulor.mediapipe.data.utils.getTodaysDate
import p4ulor.mediapipe.ui.animations.smooth
import p4ulor.mediapipe.ui.components.MaterialIcons
import p4ulor.mediapipe.ui.components.QuickAlertDialog
import p4ulor.mediapipe.ui.components.utils.HorizontalPaddingBigExtra
import p4ulor.mediapipe.ui.components.utils.LightContainer
import p4ulor.mediapipe.ui.components.utils.TransparencyGradient
import p4ulor.mediapipe.ui.components.utils.TransparentGradientPosition
import p4ulor.mediapipe.ui.components.utils.fadingEdge
import p4ulor.mediapipe.ui.components.utils.rememberToggleableState
import p4ulor.mediapipe.ui.theme.PreviewComposable

@Composable
fun TabYourAchievements(achievements: List<Achievement>, onDeleteAchievements: () -> Unit) {
    var showDeletionConfirmation = rememberToggleableState(false)
    val blurRadius: Dp by animateDpAsState(
        targetValue = if (showDeletionConfirmation.value) 20.dp else 0.dp,
        animationSpec = smooth(),
        label = "blur"
    )

    Scaffold(
        Modifier.fillMaxSize().blur(blurRadius),
        containerColor = Color.Transparent,
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { showDeletionConfirmation.toggle() }
            ) {
                with(MaterialIcons.DeleteForever){
                    Icon(this, contentDescription = name)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets(0)
    ) {
        if (showDeletionConfirmation.value) {
            DeletionConfirmationDialog(
                confirmClick = {
                    onDeleteAchievements()
                    showDeletionConfirmation.toggle()
                },
                dismissClick = {
                    showDeletionConfirmation.toggle()
                }
            )
        }
        AchievementsList(it, achievements)
    }
}

@Composable
private fun DeletionConfirmationDialog(confirmClick: () -> Unit, dismissClick: () -> Unit){
    QuickAlertDialog(
        title = R.string.warning,
        description = R.string.all_achievements_will_be_deleted,
        confirmText = R.string.yes,
        dismissText = R.string.cancel,
        confirmClick = confirmClick,
        dismissClick = dismissClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementsList(padding: PaddingValues, achievements: List<Achievement>) {
    val listState = rememberLazyListState()

    var isFirstItemNotVisible by remember { mutableStateOf(false) }
    var isLastItemNotVisible by remember { mutableStateOf(false) }

    LaunchedEffect(listState.isScrollInProgress) {
        isFirstItemNotVisible = listState.layoutInfo
            .visibleItemsInfo
            .any { it.index == 0 }.not()
        isLastItemNotVisible = listState.layoutInfo
            .visibleItemsInfo
            .any { it.index + 1 == achievements.size }.not()
    }

    LazyColumn(
        Modifier
            .padding(padding)
            .fillMaxSize()
            .fadingEdge(
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
        items(achievements, key = { it.objectName }) { achievement ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HorizontalPaddingBigExtra)
                    .background(Color.Transparent),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val toolTipState = rememberTooltipState(isPersistent = false)
                val scope = rememberCoroutineScope()
                LightContainer {
                    Text(achievement.objectName.capitalized())
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip(caretSize = TooltipDefaults.caretSize) {
                            Text(achievement.detectionDate.toString())
                        }
                    },
                    state = toolTipState
                ) {
                    Checkbox(
                        checked = achievement.detectionDate != null,
                        onCheckedChange = {
                            if(achievement.detectionDate != null){
                                scope.launch { toolTipState.show() }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TabYourAchievementsPreview() = PreviewComposable(enableDarkTheme = true) {
    val list = remember {
        buildList {
            add(Achievement("START"))
            repeat(20) {
                addAll(
                    listOf(
                        Achievement("car$it", getTodaysDate()),
                        Achievement("big word big word$it", getTodaysDate()),
                        Achievement("bench$it")
                    )
                )
            }
            add(Achievement("END"))
        }
    }
    TabYourAchievements(
        achievements = list,
        onDeleteAchievements = {}
    )
}
