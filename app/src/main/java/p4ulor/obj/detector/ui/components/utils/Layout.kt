package p4ulor.obj.detector.ui.components.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CenteredContent(content: @Composable () -> Unit){
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
fun CenteredRow(modifier: Modifier = Modifier.fillMaxWidth(), content: @Composable () -> Unit){
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = HorizontalPadding,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun BoxWithBackground(
    resourceId: Int,
    boxModifier: Modifier = Modifier.fillMaxSize(),
    contentScale: ContentScale = ContentScale.FillBounds,
    invert: Boolean = false,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    BoxWithConstraints(boxModifier) {
        Image(
            painter = painterResource(resourceId),
            contentDescription = "Background",
            Modifier
                .fillMaxSize()
                .addIfTrue(invert){
                    Modifier.graphicsLayer { scaleX = -1f } // Scale Horizontally
                },
            contentScale = contentScale
        )
        content()
    }
}

/** Involves [content] with a Box that has a light [outline] border and a light [secondaryContainer] */
@Composable
fun LightContainer(content: @Composable () -> Unit) = Box(
    Modifier
        .clip(SuperRoundRectangleShape)
        .background(color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
        .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            shape = SuperRoundRectangleShape
        )
        .padding(8.dp),
    contentAlignment = Alignment.Center
) {
    content()
}


