package p4ulor.obj.detector.data.sources.cloud.firebase

import com.google.firebase.auth.FirebaseAuth
import org.koin.core.annotation.Single
import p4ulor.obj.detector.i

@Single
class FirebaseInstance {
    private val fb = FirebaseAuth.getInstance()

    fun logCurrUser(){
        i("Obtained user ${fb.currentUser}")
    }
}
