package p4ulor.obj.detector.ui.screens.achievements.leaderboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import p4ulor.obj.detector.ui.components.IconMediumSize
import p4ulor.obj.detector.ui.components.MaterialIcons
import p4ulor.obj.detector.ui.theme.PreviewComposable

@Composable
fun UserProfilePicture(photoUri: String?, size: Dp = IconMediumSize) {
    val isValid = photoUri.orEmpty().isNotEmpty()
    AsyncImage(
        model = if(isValid) photoUri else null,
        contentDescription = "User profile picture",
        Modifier
            .size(size)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
        colorFilter = if(isValid) {
            null
        } else {
            ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
        },
        fallback = rememberVectorPainter(MaterialIcons.Person)
    )
}

@Composable
@Preview
private fun UserProfilePicturePreview() = PreviewComposable(enableDarkTheme = true) {
    UserProfilePicture(null)
}