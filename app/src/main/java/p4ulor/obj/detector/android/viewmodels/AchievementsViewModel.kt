package p4ulor.obj.detector.android.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Single
import p4ulor.obj.detector.android.MyApplication
import p4ulor.obj.detector.android.utils.NetworkObserver
import p4ulor.obj.detector.android.viewmodels.utils.launch
import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.sources.cloud.firebase.FirebaseInstance
import p4ulor.obj.detector.ui.screens.achievements.OrderOption

@SuppressLint("StaticFieldLeak") // Property ctx will be injected
@Single // So it's also not re-instantiated on composable destruction's
@KoinViewModel
class AchievementsViewModel(
    private val application: Application,
    private val network: NetworkObserver,
    private val firebase: FirebaseInstance
) : AndroidViewModel(application) {

    private val _currUser = MutableStateFlow<User?>(null)
    val currUser = _currUser.asStateFlow()

    init {
        firebase.init(application.applicationContext)
    }

    private val achievementsDao by lazy {
        getApplication<MyApplication>().appDb.achievements()
    }

    private val _userAchievements= MutableStateFlow(emptyList<Achievement>())
    val userAchievements = _userAchievements.asStateFlow()

    private val _orderOption = MutableStateFlow(OrderOption.Name)
    val orderOption =_orderOption.asStateFlow()

    fun loadAchievements() {
        launch {
            _userAchievements.value = Achievement.from(achievementsDao.getAll())
            orderAchievements()
        }
    }

    fun deleteAchievements() {
        launch {
            achievementsDao.resetAll()
            _userAchievements.value = Achievement.reset(_userAchievements.value)
        }
    }

    fun setOrderOption(options: OrderOption) {
        _orderOption.value = options
        orderAchievements()
    }

    /** Orders achievements in a coroutine to avoid using UI thread */
    private fun orderAchievements() {
        launch {
            _userAchievements.value = when (_orderOption.value) {
                OrderOption.Done -> userAchievements.value?.toMutableList()?.let {
                    it.groupBy { if (it.detectionDate != null) 0 else 1 }
                        .toSortedMap()
                        .flatMap { it.value }
                } ?: emptyList()

                OrderOption.Name -> userAchievements.value?.toMutableList()?.apply {
                    sortBy { it.objectName }
                } ?: emptyList()
            }
        }
    }

    /** Firebase */

    fun signInWithGoogle() {
        launch {
            _currUser.value = firebase.signInWithGoogle(application.applicationContext)
        }
    }

    fun signOut() {
        launch {
            firebase.signOut()
            _currUser.value = null
        }
    }
}
