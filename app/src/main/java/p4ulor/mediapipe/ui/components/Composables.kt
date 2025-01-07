package p4ulor.mediapipe.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun CenteredContent(content: @Composable () -> Unit){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
fun BoxWithBackground(
    resourceId: Int,
    boxModifier: Modifier = Modifier.fillMaxSize(),
    contentScale: ContentScale = ContentScale.FillBounds,
    content: @Composable @UiComposable BoxWithConstraintsScope.() -> Unit
) {
    BoxWithConstraints(boxModifier) {
        Image(
            painter = painterResource(resourceId),
            contentDescription = "Background",
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}
