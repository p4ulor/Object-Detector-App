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
import p4ulor.mediapipe.android.viewmodels.utils.launch
import p4ulor.mediapipe.data.domains.mediapipe.Achievement
import p4ulor.mediapipe.ui.screens.achievements.OrderOptions

@SuppressLint("StaticFieldLeak") // Property ctx will be injected
@Single // So it's also not re-instantiated on composable destruction's
@KoinViewModel
class AchievementsViewModel(
    application: Application,
    private val network: NetworkObserver
) : AndroidViewModel(application) {

    private val achievementsDao by lazy {
        getApplication<MyApplication>().appDb.achievements()
    }

    private val _userAchievements= MutableStateFlow(emptyList<Achievement>())
    val userAchievements = _userAchievements.asStateFlow()

    private val _orderOptions = MutableStateFlow(OrderOptions.Name)
    val orderOptions =_orderOptions.asStateFlow()

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

    fun setOrderOption(options: OrderOptions) {
        _orderOptions.value = options
        orderAchievements()
    }

    /** Order achievements in a coroutine to avoid using UI thread */
    private fun orderAchievements() {
        launch {
            _userAchievements.value = when (_orderOptions.value) {
                OrderOptions.Done -> userAchievements.value?.toMutableList()?.let {
                    it.groupBy { if (it.detectionDate != null) 0 else 1 }
                        .toSortedMap()
                        .flatMap { it.value }
                } ?: emptyList()
                else -> userAchievements.value?.toMutableList()?.apply {
                    sortBy { it.objectName }
                } ?: emptyList()
            }
        }
    }
}
