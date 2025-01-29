package p4ulor.mediapipe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import p4ulor.mediapipe.R

// Default
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

fun geminiLikeText(text: String) = buildAnnotatedString {
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
        append(text)
    }
}

fun mediaPipeLikeText(text: String) = buildAnnotatedString {
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
        append(text)
    }
}

@Preview
@Composable
fun geminiLikeTextPreview(){
    Text(
        geminiLikeText(stringResource(R.string.gemini_api_key)),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

