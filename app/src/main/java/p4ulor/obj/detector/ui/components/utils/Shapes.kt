package p4ulor.obj.detector.ui.components.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val RoundRectangleShape: Shape = RoundedCornerShape(30f)
val SuperRoundRectangleShape: Shape = RoundedCornerShape(100f)

fun roundMessageBox(authorIsUser: Boolean) = if (authorIsUser) {
    RoundedCornerShape(20.dp, 4.dp, 20.dp, 40.dp)
} else {
    RoundedCornerShape(4.dp, 20.dp, 40.dp, 20.dp)
}