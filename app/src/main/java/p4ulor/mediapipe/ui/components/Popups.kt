package p4ulor.mediapipe.ui.components

import android.content.Context
import android.widget.Toast

fun Context.toast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}