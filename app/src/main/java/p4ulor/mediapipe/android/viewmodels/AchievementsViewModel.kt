package p4ulor.mediapipe.android.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Single
import p4ulor.mediapipe.android.MyApplication
import p4ulor.mediapipe.android.utils.NetworkObserver
import p4ulor.mediapipe.android.utils.NotificationManager
import p4ulor.mediapipe.android.viewmodels.utils.launch
import p4ulor.mediapipe.data.domains.mediapipe.Achievement
import p4ulor.mediapipe.data.domains.mediapipe.UserAchievements

@SuppressLint("StaticFieldLeak") // Property ctx will be injected
@Single // So it's also not re-instantiated on composable destruction's
@KoinViewModel
class AchievementsViewModel(
    private val application: Application,
    private val network: NetworkObserver,
    private val notificationManager: NotificationManager,
) : AndroidViewModel(application) {

    private val achievementsDao by lazy { getApplication<MyApplication>().appDb.achivements() }

    private val _userAchievements= MutableStateFlow<UserAchievements?>(null)
    val userAchievements = _userAchievements.asStateFlow()
    
    fun loadAchievements() {
        launch {
            _userAchievements.value = UserAchievements(
                achievements = Achievement.from(achievementsDao.getAll())
            )
        }
    }

    fun sendAchievementNotification(title: String, description: String) =
        notificationManager.sendAchievementNotification(title, description)
}