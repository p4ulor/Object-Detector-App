package p4ulor.obj.detector.ui.screens.achievements.local

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import p4ulor.obj.detector.data.domains.mediapipe.getDonePercentage
import p4ulor.obj.detector.data.utils.capitalized
import p4ulor.obj.detector.data.utils.getTodaysDate
import p4ulor.obj.detector.data.utils.toPercentage
import p4ulor.obj.detector.ui.animations.smooth
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.QuickText
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.LightCircularContainer
import p4ulor.obj.detector.ui.components.utils.TransparencyGradient
import p4ulor.obj.detector.ui.components.utils.TransparentGradientPosition
import p4ulor.obj.detector.ui.components.utils.fadingEdge
import p4ulor.obj.detector.ui.components.utils.rememberToggleableState
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun TabYourAchievements(
    achievements: List<Achievement>,
    orderOptions: OrderOption,
    onDeleteAchievements: () -> Unit,
    onChangeOrderOption: (OrderOption) -> Unit
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
        animationSpec = smooth(durationMillis = 800, delayMillis = 300),
        label = "donePercentage"
    )

    LaunchedEffect(achievements.hashCode()) {
        donePercentageValue = achievements.getDonePercentage()
    }

    Scaffold(
        Modifier
            .fillMaxSize()
            .blur(blurRadius),
        topBar = {
            CenteredRow {
                QuickText(R.string.order_by, fontWeight = FontWeight.Bold)

                SingleChoiceSegmentedButtonRow {
                    OrderOption.entries.forEachIndexed { index, option ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index, OrderOption.entries.size),
                            onClick = { onChangeOrderOption(OrderOption.entries[index]) },
                            selected = index == orderOptions.ordinal,
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

        AchievementsList(paddingValues, achievements, orderOptions)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementsList(
    padding: PaddingValues, // because of the topBar
    achivements: List<Achievement>,
    selectedOrderOption: OrderOption
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
            .padding(padding) // because of this, the amount of fade out that's visible is not the same as top because the list itself is pushed down (because of the topBar), so we need to provide this padding to the TransparencyGradient
            .fillMaxSize()
            .fadingEdge(
                TransparencyGradient(
                    position = when {
                        isFirstItemNotVisible && isLastItemNotVisible -> TransparentGradientPosition.TopAndBottom
                        isFirstItemNotVisible -> TransparentGradientPosition.Top
                        isLastItemNotVisible -> TransparentGradientPosition.Bottom
                        else -> TransparentGradientPosition.None
                    },
                    extraBottomPadding = padding.calculateTopPadding()
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = listState,
    ) {
        items(achivements, key = { it.objectName }) { achievement ->
            CenteredRow(horizontalPadding = 0.dp /* cuz Checkbox already has a good padding by default */) {
                val toolTipState = rememberTooltipState(isPersistent = false)
                val scope = rememberCoroutineScope() // Needed for TooltipBox...

                LightCircularContainer {
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
                        },
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
        orderOptions = OrderOption.Name,
        onChangeOrderOption = {},
        onDeleteAchievements = {}
    )
}
