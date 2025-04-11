package p4ulor.mediapipe.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import p4ulor.mediapipe.R
import p4ulor.mediapipe.ui.theme.PreviewComposable

@Composable
fun QuickAlertDialog(
    @StringRes title: Int,
    @StringRes description: Int,
    @StringRes confirmText: Int,
    @StringRes dismissText: Int,
    confirmClick: () -> Unit,
    dismissClick: () -> Unit,
){
    AlertDialog(
        onDismissRequest = dismissClick,
        icon = { QuickIcon(MaterialIcons.Warning) { }},
        title = { QuickText(title) },
        text = { QuickText(description) },
        confirmButton = {
            TextButton(onClick = confirmClick) { QuickText(confirmText) }
        },
        dismissButton = {
            TextButton(onClick = dismissClick) { QuickText(dismissText) }
        }
    )
}

@Preview
@Composable
private fun QuickAlertDialogPreview() = PreviewComposable(enableDarkTheme = true) {
    QuickAlertDialog(
        R.string.warning,
        R.string.all_achievements_will_be_deleted,
        R.string.yes,
        R.string.cancel,
        {},
        {}
    )
}
