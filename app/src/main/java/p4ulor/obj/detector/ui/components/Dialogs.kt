package p4ulor.obj.detector.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import p4ulor.obj.detector.R
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun EzAlertDialog(
    @StringRes title: Int,
    @StringRes description: Int,
    @StringRes confirmText: Int,
    @StringRes dismissText: Int,
    confirmClick: () -> Unit,
    dismissClick: () -> Unit,
    icon: Icon = Icon.Material(MaterialIcons.Warning)
){
    AlertDialog(
        onDismissRequest = dismissClick,
        icon = { EzIcon(icon) { } },
        title = { EzText(title) },
        text = { EzText(description) },
        confirmButton = {
            TextButton(onClick = confirmClick) { EzText(confirmText) }
        },
        dismissButton = {
            TextButton(onClick = dismissClick) { EzText(dismissText) }
        }
    )
}

@Preview
@Composable
private fun EzAlertDialogPreview() = PreviewComposable(enableDarkTheme = true) {
    EzAlertDialog(
        R.string.warning,
        R.string.all_achievements_will_be_deleted,
        R.string.yes,
        R.string.cancel,
        {},
        {}
    )
}
