package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import p4ulor.obj.detector.R
import p4ulor.obj.detector.data.domains.firebase.UserAchievement
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.domains.mediapipe.pointsDifferenceBetween
import p4ulor.obj.detector.ui.animations.rememberBouncyFloatLooper
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.EzText
import p4ulor.obj.detector.ui.components.superGlowText
import p4ulor.obj.detector.ui.components.utils.CenteredColumn
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.GeneralPadding
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun LeaderboardMainActions(
    localAchievements: List<Achievement>,
    submittedAchievements: List<UserAchievement>?,
    onSubmitAchievements: () -> Unit
) {
    val animatedPointsY = rememberBouncyFloatLooper(initialValue = 0f, targetValue = -10f)
    var pointsDiff by rememberSaveable { mutableFloatStateOf(0f) }

    LaunchedEffect(localAchievements.hashCode(), submittedAchievements.hashCode()) {
        pointsDiff = localAchievements.pointsDifferenceBetween(submittedAchievements)
    }

    CenteredRow(Modifier.padding(horizontal = GeneralPadding)) {
        LeaderboardAction(
            MaterialIcons.Publish,
            R.string.submit_achiv_action,
            R.string.submit_achiv_action_desc,
            R.string.submit
        ) {
            onSubmitAchievements()
        }

        if (pointsDiff != 0f) {
            val (directionalDiff, color) = if (pointsDiff >= 0f) {
                "+" to Color.White
            } else {
                "" to Color.Red
            }
            superGlowText(
                "$directionalDiff${pointsDiff}",
                Modifier.offset(y = animatedPointsY.value.dp),
                blurRadius = Math.pow(4.0, animatedPointsY.value.toDouble()).toFloat(),
                color = color
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderboardAction(
    icon: ImageVector,
    @StringRes title: Int,
    @StringRes description: Int,
    @StringRes actionName: Int,
    performAction: () -> Unit
) {
    val toolTipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                Modifier.padding(horizontal = GeneralPadding),
                title = { EzText(title, fontWeight = FontWeight.ExtraBold) },
                action = {
                    TextButton(onClick = {
                        performAction()
                        toolTipState.dismiss()
                    }) {
                        EzText(actionName)
                    }
                },
                caretSize = TooltipDefaults.caretSize * 2f
            ) {
                EzText(description)
            }
        },
        state = toolTipState
    ) {
        SmallFloatingActionButton(
            onClick = { scope.launch { toolTipState.show() } },
        ) {
            with(icon) {
                Icon(this, contentDescription = name)
            }
        }
    }
}

@Preview
@Composable
private fun LeaderBoardActionsPreview() = PreviewComposable {
    CenteredColumn {
        LeaderboardMainActions(
            localAchievements = listOf(
                Achievement("cat", 0.8f),
                Achievement("car", 0.3f),
            ),
            submittedAchievements = listOf(UserAchievement("cat", 0.8f)),
            onSubmitAchievements = {},
        )

        LeaderboardMainActions(
            localAchievements = listOf(Achievement("cat", 0.8f)),
            submittedAchievements = listOf(UserAchievement("cat", 0.8f)),
            onSubmitAchievements = {},
        )

        LeaderboardMainActions(
            localAchievements = listOf(Achievement("cat", 0.2f)),
            submittedAchievements = listOf(UserAchievement("cat", 0.8f)),
            onSubmitAchievements = {},
        )
    }
}

