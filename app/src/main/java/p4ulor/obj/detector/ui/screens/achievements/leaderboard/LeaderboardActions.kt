package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.PersonOff
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import p4ulor.obj.detector.R
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.QuickText
import p4ulor.obj.detector.ui.components.utils.CenteredRow
import p4ulor.obj.detector.ui.components.utils.GeneralPadding
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingTiny
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun LeaderboardActions(
    onSubmitAchievements: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    CenteredRow(Modifier.padding(bottom = GeneralPaddingTiny)) {
        LeaderboardAction(
            MaterialIcons.Publish,
            R.string.submit_achiv_action,
            R.string.submit_achiv_action_desc,
            R.string.submit
        ) {
            onSubmitAchievements()
        }

        LeaderboardAction(
            MaterialIcons.PersonOff,
            R.string.delete_acc_action,
            R.string.delete_acc__action_desc,
            R.string.delete
        ) {
            onDeleteAccount()
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
    onContinueAction: () -> Unit
) {
    val toolTipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                Modifier.padding(horizontal = GeneralPadding),
                title = { QuickText(title, fontWeight = FontWeight.Bold) },
                action = {
                    TextButton(onClick = {
                        onContinueAction()
                        toolTipState.dismiss()
                    }) {
                        QuickText(actionName)
                    }
                },
                caretSize = TooltipDefaults.caretSize * 2f
            ) {
                QuickText(description)
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
private fun LeaderBoardActionsPreview() = PreviewComposable (enableDarkTheme = true) {
    LeaderboardActions(
        onSubmitAchievements = {},
        onDeleteAccount = {}
    )
}
