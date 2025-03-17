package p4ulor.mediapipe.ui.components.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Shows the composable via [drawContent], but only where the two images overlap according to
 * [transparencyGradient].
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
