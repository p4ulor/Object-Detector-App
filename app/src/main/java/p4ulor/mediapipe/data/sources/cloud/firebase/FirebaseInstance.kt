package p4ulor.mediapipe.data.sources.cloud.firebase

import com.google.firebase.auth.FirebaseAuth
import org.koin.core.annotation.Single

@Single
class FirebaseInstance {
    private val fb = FirebaseAuth.getInstance()
    fun test(){
        print("obtained user ${fb.currentUser}")
    }
}