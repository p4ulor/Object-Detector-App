package p4ulor.mediapipe.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    content: @Composable (BoxScope.() -> Unit)
) {
    Box(boxModifier) {
        Image(
            painter = painterResource(resourceId),
            contentDescription = "Background",
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}
