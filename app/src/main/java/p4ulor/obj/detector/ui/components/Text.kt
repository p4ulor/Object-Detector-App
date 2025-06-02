package p4ulor.obj.detector.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import p4ulor.obj.detector.R
import p4ulor.obj.detector.ui.components.utils.GeminiLikeGradient
import p4ulor.obj.detector.ui.components.utils.GeneralPaddingSmall
import p4ulor.obj.detector.ui.components.utils.MediaPipeLikeGradient
import p4ulor.obj.detector.ui.theme.AppTheme
import androidx.compose.material3.Text as ComposeText

/** Because it's tiring to have to write [stringResource] */
@Composable
fun QuickText(
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    maxLines: Int = Int.MAX_VALUE,
    textStyle: TextStyle = LocalTextStyle.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextvOverflow: () -> Unit = {}
) = ComposeText(
    stringResource(text), modifier, color, fontSize, fontStyle, fontWeight, fontFamily,
    maxLines = maxLines,
    style = textStyle,
    overflow = overflow,
    onTextLayout = {
        if (it.didOverflowWidth) {
            onTextvOverflow()
        }
    }
)

@Composable
fun geminiLikeText(@StringRes text: Int) = buildAnnotatedString {
    withStyle(
        style = SpanStyle(
            brush = Brush.linearGradient(
                colors = GeminiLikeGradient,
                start = Offset(100f, 100f),
                end = Offset(300f, -100f)
            ),
            shadow = Shadow(blurRadius = 20f)
        )
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
            shadow = Shadow(blurRadius = 10f)
        )
    ) {
        append(stringResource(text))
    }
}

@Composable
fun glowText(text: String, blurRadius: Float = 10f) = buildAnnotatedString {
    withStyle(
        style = SpanStyle(
            shadow = Shadow(Color.White, blurRadius = blurRadius)
        )
    ) {
        append(text)
    }
}

@Composable
fun superGlowText(
    text: String, modifier:
    Modifier = Modifier,
    blurRadius: Float = 20f,
    fontWeight: FontWeight? = null,
    textStyle: TextStyle = LocalTextStyle.current
) {
    Box(modifier) {
        Text(
            glowText(text, blurRadius = blurRadius),
            Modifier.graphicsLayer {
                renderEffect = BlurEffect(
                    radiusX = 10f,
                    radiusY = 10f,
                    edgeTreatment = TileMode.Decal
                )
            },
            fontWeight = fontWeight,
            style = textStyle,
        )

        Text(
            glowText(text, blurRadius = blurRadius),
            fontWeight = fontWeight,
            style = textStyle,
        )
    }
}

@Preview
@Composable
private fun TextPreviews() = AppTheme(enableDarkTheme = true){
    Surface {
        Column(Modifier.padding(GeneralPaddingSmall)) {
            Text(
                mediaPipeLikeText(R.string.mediapipe),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                geminiLikeText(R.string.gemini_api),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                glowText("Glow text"),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                glowText("Glow text"),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                glowText("Blurred text"),
                Modifier.graphicsLayer {
                    renderEffect = BlurEffect(
                        radiusX = 10f,
                        radiusY = 10f,
                        edgeTreatment = TileMode.Decal
                    )
                },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
            )

            superGlowText(
                "Glow text",
                textStyle = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}
