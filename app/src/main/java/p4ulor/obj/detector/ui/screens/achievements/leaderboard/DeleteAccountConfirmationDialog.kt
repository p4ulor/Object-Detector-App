package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.runtime.Composable
import p4ulor.obj.detector.R
import p4ulor.obj.detector.ui.components.Icon
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.EzAlertDialog

@Composable
fun DeletionConfirmationDialog(confirmClick: () -> Unit, dismissClick: () -> Unit){
    EzAlertDialog(
        R.string.delete_acc_action,
        R.string.delete_acc_action_desc,
        R.string.yes,
        R.string.cancel,
        confirmClick = confirmClick,
        dismissClick = dismissClick,
        Icon.Material(MaterialIcons.PersonOff),
    )
}