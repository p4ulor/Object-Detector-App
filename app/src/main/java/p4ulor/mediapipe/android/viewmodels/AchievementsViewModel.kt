package p4ulor.mediapipe.android.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Single
import p4ulor.mediapipe.android.utils.NetworkObserver
import p4ulor.mediapipe.android.utils.NotificationManager

@SuppressLint("StaticFieldLeak") // Property ctx will be injected
@Single // So it's also not re-instantiated on composable destruction's
@KoinViewModel
class AchievementsViewModel(
    private val ctx: Context,
    private val network: NetworkObserver,
    private val notificationManager: NotificationManager,
) : ViewModel() {
    fun sendAchievementNotification(title: String, description: String) =
        notificationManager.sendAchievementNotification(title, description)
}