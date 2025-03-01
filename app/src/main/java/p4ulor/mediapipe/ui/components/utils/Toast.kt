package p4ulor.mediapipe.ui.components.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes

/** Creates a Toast that uses the Main/UI thread (as required when using Toast) */
fun Context.toast(@StringRes text: Int, param: String = "") {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(this, this.getString(text, param), Toast.LENGTH_SHORT).show()
    }
}
