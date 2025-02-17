package p4ulor.mediapipe.ui.components.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

/** Must be called in Main/UI thread */
fun Context.toast(text: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
