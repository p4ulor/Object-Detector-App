package p4ulor.obj.detector.ui.screens.achievements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import p4ulor.obj.detector.R
import p4ulor.obj.detector.android.utils.getResourcePath
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.theme.PreviewComposable

/**
 * An official and permissible logo use case for login purposes
 * https://developers.google.com/identity/branding-guidelines
 * Note: is not previewable through AS
 */
@Composable
fun SignInWithGoogle(modifier: Modifier = Modifier.fillMaxSize(), onClick: () -> Unit) {
    val ctx = LocalContext.current
    val logo = if (isSystemInDarkTheme()) R.raw.android_dark_rd_ctn else R.raw.android_light_rd_ctn

    AsyncImage(
        model = ImageRequest.Builder(ctx)
            .data(ctx.getResourcePath(logo))
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = "Google Logo",
        modifier.clickable { onClick() },
        error = painterResource(R.drawable.flashlight_off),
        onError = {
            i("Error loading SignInWithGoogle: $it")
        }
    )

    /**
     * This causes HTTP 504. To fix it, it would require using `coil-network-okhttp` and setting
     * up a `OkHttpClient` with a custom connection, read and write timeout and use it in the
     * ImageLoader.Builder ... So I'm using the local .svg
     */
    /*AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://developers.google.com/static/identity/images/branding_guideline_sample_dk_rd_lg.svg")
            .decoderFactory(SvgDecoder.Factory())
            .diskCachePolicy(CachePolicy.DISABLED)
            .build(),
        contentDescription = null,
        Modifier.size(48.dp),
        error = painterResource(R.drawable.gemini),
        onError = {
            i("Error loading SignInWithGoogle2: $it")
        }
    )*/
}

/** Will only display on real device, not in AS compose preview */
@Preview
@Composable
private fun SignInWithGooglePreview() = PreviewComposable(enableDarkTheme = true) {
    SignInWithGoogle(onClick = {})
}