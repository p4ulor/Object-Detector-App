package p4ulor.mediapipe.ui.components.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Shows the composable via [drawContent], but only where the two images overlap according to
 * [transparencyGradient], which should be a black to white or white to black gradient.
 *
 * - [graphicsLayer]  renders the content of this modifier in a separate layer. Using
 * [CompositingStrategy.Offscreen] ensures that [BlendMode.DstIn] is applied in an offscreen buffer
 * (that's isolated from other composables) and only then it will be rendered on top, which ensures
 * the transparency is applied
 *
 * - [drawWithContent] is used to overlay a rectangle with a [transparencyGradient]. And with
 * [BlendMode.DstIn], it will be used as a visibility mask. Meaning that the [drawContent] will only be
 * visible in areas where the content and rectangle overlap (which should be a non-transparent color)
 *
 * - https://developer.android.com/develop/ui/compose/graphics/draw/modifiers#compositing-strategy
 */
fun Modifier.fadingEdge(transparencyGradient: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = transparencyGradient, blendMode = BlendMode.DstIn)
    }


fun Modifier.withGradientBehind(gradient: Brush) = this
    .drawWithCache {
        onDrawBehind {
            drawRoundRect(
                gradient,
                //cornerRadius = CornerRadius(15.dp.toPx())
            )
        }
    }

/** Util to only add changes to a [Modifier] is [value] is not null to avoid unused processing */
fun<T> Modifier.addIfNotNull(value: T?, modifierOp: Modifier.(value: T) -> Modifier) =
    then(
        if(value != null){
            this.modifierOp(value)
        } else {
            Modifier
        }
    )

/** Util to only apply a [modifierOp] if [condition] is true, in order to reduce lines and improve readability */
@Composable
fun Modifier.addIfTrue(condition: Boolean, modifierOp: @Composable Modifier.() -> Modifier) =
    then(
        if(condition){
            this.modifierOp()
        } else {
            Modifier
        }
    )
