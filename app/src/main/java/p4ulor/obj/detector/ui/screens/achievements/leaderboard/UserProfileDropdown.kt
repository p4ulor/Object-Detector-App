package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import p4ulor.obj.detector.ui.animations.bouncySpring
import p4ulor.obj.detector.ui.components.Icon
import p4ulor.obj.detector.ui.components.IconDefaultSize
import p4ulor.obj.detector.ui.components.IconMediumSize
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.components.MaterialIconsExt
import p4ulor.obj.detector.ui.components.QuickIcon
import p4ulor.obj.detector.ui.components.utils.CenteredColumn
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingTiny
import p4ulor.obj.detector.ui.components.utils.RoundRectangleShape
import p4ulor.obj.detector.ui.components.utils.VerticalPadding
import p4ulor.obj.detector.ui.theme.PreviewComposable

private val ExpanderIconSize = IconDefaultSize
private val ActionIconSize = IconMediumSize

@Composable
fun UserProfileDropdown(
    photoUri: String?,
    dropDownActions: List<DropdownAction>,
) {
    var isExpanded by remember { mutableStateOf(false) }

    val expandedBoxSize = remember {
        (dropDownActions.size * ExpanderIconSize.value).dp
    }

    val expandedContainerHeight by animateDpAsState(
        targetValue = if (isExpanded) expandedBoxSize else 0.dp,
        animationSpec = bouncySpring()
    )

    CenteredColumn (Modifier) {
        Box(Modifier
            .clip(CircleShape) // makes so the selection area is also a circle
            .clickable { isExpanded = !isExpanded }
        ) {
            UserProfilePicture(photoUri, ExpanderIconSize)
        }

        AnimatedVisibility( // With this, it fixes the weird behaviour from the Popup of starting the dropdown animation from the top of the screen when it's opened on the first time. With the column there's no longer the need to setup the IntOffset. The column will not consider this as occupying space. See commit 06a6df48ad96e6ef95d1424cda482878f8f60252
            visible = isExpanded,
            enter = EnterTransition.None,
            exit = fadeOut()
        ) {
            Popup( // because wrapContentSize(unbounded = true) will not work so the dropdown extends beyond the composable bounds
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, 0),
                onDismissRequest = { },
            ) {
                Box(Modifier
                    .padding(top = GeneralPaddingTiny)
                    .size(
                        width = ExpanderIconSize,
                        height = expandedContainerHeight
                    )
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundRectangleShape
                    )
                ) {
                    LazyColumn (
                        Modifier.align(Alignment.Center),
                        verticalArrangement = Arrangement.spacedBy(VerticalPadding)
                    ) {
                        items(dropDownActions) {
                            QuickIcon(
                                icon = it.icon,
                                size = ActionIconSize,
                                padding = 0.dp,
                                onClick = {
                                    it.action()
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class DropdownAction(
    val icon: Icon,
    val action: () -> Unit
)

@Composable
@Preview
private fun UserProfileDropdownPreview() = PreviewComposable (enableDarkTheme = true) {
    UserProfileDropdown(
        photoUri = null,
        dropDownActions = listOf(
            DropdownAction(Icon.Material(MaterialIconsExt.Logout)) {},
            DropdownAction(Icon.Material(MaterialIcons.PersonOff)) {}
        )
    )
}
