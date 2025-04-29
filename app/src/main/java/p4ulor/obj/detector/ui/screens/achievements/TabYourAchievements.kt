package p4ulor.obj.detector.ui.screens.achievements

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import p4ulor.obj.detector.R
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.utils.capitalized
import p4ulor.obj.detector.data.utils.getTodaysDate
import p4ulor.obj.detector.data.utils.toPercentage
import p4ulor.obj.detector.ui.animations.smooth
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.QuickAlertDialog
import p4ulor.obj.detector.ui.components.QuickText
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.HorizontalPaddingBigExtra
import p4ulor.obj.detector.ui.components.utils.LightContainer
import p4ulor.obj.detector.ui.components.utils.TransparencyGradient
import p4ulor.obj.detector.ui.components.utils.TransparentGradientPosition
import p4ulor.obj.detector.ui.components.utils.fadingEdge
import p4ulor.obj.detector.ui.components.utils.rememberToggleableState
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun TabYourAchievements(
    achievements: List<Achievement>,
    selectedOrderOption: OrderOptions,
    onDeleteAchievements: () -> Unit,
    onChangeOrderOption: (OrderOptions) -> Unit
) {
    var showDeletionConfirmation = rememberToggleableState(false)
    var donePercentageValue by rememberSaveable { mutableFloatStateOf(0f) }

    val blurRadius: Dp by animateDpAsState(
        targetValue = if (showDeletionConfirmation.value) 20.dp else 0.dp,
        animationSpec = smooth(),
        label = "blurRadius"
    )

    val donePercentage: Float by animateFloatAsState(
        targetValue = donePercentageValue,
        animationSpec = smooth(delayMillis = 500),
        label = "donePercentage"
    )

    LaunchedEffect(achievements.hashCode()) {
        donePercentageValue = Achievement.getDonePercentage(achievements)
    }

    Scaffold(
        Modifier
            .fillMaxSize()
            .blur(blurRadius),
        topBar = {
            CenteredRow {
                QuickText(R.string.order_by, fontWeight = FontWeight.Bold)

                SingleChoiceSegmentedButtonRow {
                    OrderOptions.entries.forEachIndexed { index, option ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index, OrderOptions.entries.size),
                            onClick = { onChangeOrderOption(OrderOptions.entries[index]) },
                            selected = index == selectedOrderOption.ordinal,
                            colors = SegmentedButtonDefaults.colors().copy(
                                activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                inactiveContainerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            QuickText(option.strId)
                        }
                    }
                }

                Box {
                    CircularProgressIndicator(
                        progress = { donePercentage },
                    )
                    Text(
                        donePercentage.toPercentage(),
                        Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        },
        containerColor = Color.Transparent,
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { showDeletionConfirmation.toggle() }
            ) {
                with(MaterialIcons.DeleteForever) {
                    Icon(this, contentDescription = name)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
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

        AchievementsList(paddingValues, achievements, selectedOrderOption)
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
private fun AchievementsList(
    padding: PaddingValues,
    achivements: List<Achievement>,
    selectedOrderOption: OrderOptions
) {
    val listState = rememberLazyListState()

    var isFirstItemNotVisible by remember { mutableStateOf(false) }
    var isLastItemNotVisible by remember { mutableStateOf(false) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.layoutInfo.totalItemsCount != 0) { // if list has been populated
            isFirstItemNotVisible = listState.layoutInfo
                .visibleItemsInfo
                .any { it.index == 0 }.not()
            isLastItemNotVisible = listState.layoutInfo
                .visibleItemsInfo
                .any { it.index + 1 == achivements.size }.not()
        }
    }

    LaunchedEffect(selectedOrderOption) {
        isFirstItemNotVisible = false
        listState.scrollToItem(0)
    }

    LazyColumn(
        Modifier
            .padding(padding)
            .fillMaxSize()
            .fadingEdge(
                TransparencyGradient(
                    position = when {
                        isFirstItemNotVisible && isLastItemNotVisible -> TransparentGradientPosition.TopAndBottom
                        isFirstItemNotVisible -> TransparentGradientPosition.Top
                        isLastItemNotVisible -> TransparentGradientPosition.Bottom
                        else -> TransparentGradientPosition.None
                    }
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = listState,
    ) {
        items(achivements, key = { it.objectName }) { achievement ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HorizontalPaddingBigExtra)
                    .background(Color.Transparent),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val toolTipState = rememberTooltipState(isPersistent = false)
                val scope = rememberCoroutineScope() // Needed for TooltipBox...

                LightContainer {
                    val rowEntry = StringBuilder(achievement.objectName.capitalized())
                    if (achievement.detectionDate != null){
                        rowEntry.append(" (${achievement.certaintyScore.toPercentage()})")
                    }
                    Text(rowEntry.toString())
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

enum class OrderOptions(@StringRes val strId: Int) {
    Name(R.string.name),
    Done(R.string.done)
}

@Preview
@Composable
private fun TabYourAchievementsPreview() = PreviewComposable(enableDarkTheme = true) {
    val list = remember {
        buildList {
            add(Achievement("START", 0f))
            repeat(20) {
                addAll(
                    listOf(
                        Achievement("car$it", 1f, getTodaysDate()),
                        Achievement("big word big word$it", 0.5f, getTodaysDate()),
                        Achievement("bench$it", 0f)
                    )
                )
            }
            add(Achievement("END", 0f))
        }
    }
    TabYourAchievements(
        achievements = list,
        selectedOrderOption = OrderOptions.Name,
        onChangeOrderOption = {},
        onDeleteAchievements = {}
    )
}
