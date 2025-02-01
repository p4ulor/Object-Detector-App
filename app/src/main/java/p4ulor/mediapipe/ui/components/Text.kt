package p4ulor.mediapipe.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import p4ulor.mediapipe.ui.theme.GeminiLikeGradient
import p4ulor.mediapipe.ui.theme.MediaPipeLikeGradient
import androidx.compose.material3.Text as ComposeText

@Composable
fun QuickText(
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
) = ComposeText(
    stringResource(text), modifier, color, fontSize, fontStyle, fontWeight, fontFamily
)

@Composable
fun geminiLikeText(@StringRes text: Int) = buildAnnotatedString {
    withStyle(
        style = SpanStyle(
            brush = Brush.linearGradient(
                colors = GeminiLikeGradient,
                start = Offset(100f, 0f),
                end = Offset(500f, -100f)
            ),
            shadow = Shadow(blurRadius = 20f)
        ),
    ) {
        append(stringResource(text))
    }
}

@Composable
fun mediaPipeLikeText(@StringRes text: Int) = buildAnnotatedString {
    withStyle(
        style = SpanStyle(
            brush = Brush.linearGradient(
                colors = MediaPipeLikeGradient,
                start = Offset(100f, 0f),
                end = Offset(500f, -100f)
            ),
            shadow = Shadow(blurRadius = 20f)
        ),
    ) {
        append(stringResource(text))
    }
}
