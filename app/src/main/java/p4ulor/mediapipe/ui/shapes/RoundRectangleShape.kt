package p4ulor.mediapipe.ui.shapes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/** Inspired on [RectangleShape], but [RoundedCornerShape] could also been used */
val RoundRectangleShape: Shape = object : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rounded(RoundRect(
            size.toRect(),
            CornerRadius(30.0f)
        ))

    override fun toString(): String = "RectangleShape"
}