package p4ulor.mediapipe.android.activities.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/** [Same as google.accompanist](https://stackoverflow.com/a/65243835/28417805) */
fun Context.getActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}