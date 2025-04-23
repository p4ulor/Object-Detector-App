package p4ulor.obj.detector.ui.components.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SmoothHorizontalDivider(
    color: Color = Color.White,
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    cornerRadius: Dp = 2.dp
) {
    BoxWithConstraints {
        SmoothHorizontalDividerCustom(
            this.maxWidth,
            color,
            modifier,
            thickness,
            cornerRadius
        )
    }
}

@Composable
fun SmoothHorizontalDividerCustom(
    width: Dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    cornerRadius: Dp = 2.dp
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(thickness)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color, // Opaque at the center
                        color.copy(alpha = 0f) // Transparent at the edges
                    ),
                    radius = width.value * 2 - (width.value / 2)
                )
            )
            .padding(3.dp)
    )
}

@Preview
@Composable
private fun SmoothHorizontalDividerPreview(){
    Box(Modifier.size(100.dp, 100.dp)) {
        CenteredContent {
            SmoothHorizontalDivider(Color(0xFFFFFFFF))
        }
    }
}
