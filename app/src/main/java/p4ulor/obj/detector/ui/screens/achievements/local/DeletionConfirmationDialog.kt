package p4ulor.obj.detector.ui.screens.achievements.local

import androidx.compose.runtime.Composable
import p4ulor.obj.detector.R
import p4ulor.obj.detector.ui.components.QuickAlertDialog

@Composable
fun DeletionConfirmationDialog(confirmClick: () -> Unit, dismissClick: () -> Unit){
    QuickAlertDialog(
        title = R.string.warning,
        description = R.string.all_achievements_will_be_deleted,
        confirmText = R.string.yes,
        dismissText = R.string.cancel,
        confirmClick = confirmClick,
        dismissClick = dismissClick
    )
}
