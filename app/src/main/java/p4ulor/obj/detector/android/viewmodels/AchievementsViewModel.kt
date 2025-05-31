package p4ulor.obj.detector.android.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Single
import p4ulor.obj.detector.android.MyApplication
import p4ulor.obj.detector.android.utils.NetworkObserver
import p4ulor.obj.detector.android.viewmodels.utils.launch
import p4ulor.obj.detector.data.domains.firebase.ObjectDetectionStats
import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.data.domains.firebase.UserAchievement
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.domains.mediapipe.calculatePoints
import p4ulor.obj.detector.data.domains.mediapipe.isDifferentThan
import p4ulor.obj.detector.data.domains.mediapipe.reset
import p4ulor.obj.detector.data.domains.mediapipe.toUserAchievements
import p4ulor.obj.detector.data.sources.cloud.firebase.FirebaseInstance
import p4ulor.obj.detector.data.utils.ConnectionStatus
import p4ulor.obj.detector.e
import p4ulor.obj.detector.i
import p4ulor.obj.detector.ui.screens.achievements.LeaderboardState
import p4ulor.obj.detector.ui.screens.achievements.Tab
import p4ulor.obj.detector.ui.screens.achievements.YourAchievementsState
import p4ulor.obj.detector.ui.screens.achievements.local.OrderOption

@SuppressLint("StaticFieldLeak") // Property ctx will be injected
@Single // So it's also not re-instantiated on composable destruction's (KoinViewModel isn't enough)
@KoinViewModel
class AchievementsViewModel(
    private val application: Application,
    private val network: NetworkObserver,
    private val firebase: FirebaseInstance
) : AndroidViewModel(application) {

    private val _yourAchievements = MutableStateFlow(
        YourAchievementsState(
            achievements = emptyList(),
            orderOptions = OrderOption.Name
        )
    )
    val yourAchievements = _yourAchievements.asStateFlow()

    // Cached ordering
    private var userAchievementsOrderedByName = emptyList<Achievement>()
    private var userAchievementsOrderedByDone = emptyList<Achievement>()

    private val _leaderboard = MutableStateFlow(
        LeaderboardState(
            currUser = null,
            topUsers = emptyList(),
            topObjects = emptyList(),
            connectionStatus = ConnectionStatus.Off
        )
    )
    val leaderboard = _leaderboard.asStateFlow()

    private val _selectedTab = MutableStateFlow(Tab.YourAchievements)
    val selectedTab = _selectedTab.asStateFlow()

    private val achievementsDao by lazy {
        getApplication<MyApplication>().appDb.achievements()
    }

    init {
        firebase.init(application.applicationContext)

        launch {
            network.hasConnection.collect { hasConnection ->
                if(!hasConnection) {
                    if (leaderboard.value.currUser != null){
                        setLeaderboard(connectionStatus = ConnectionStatus.Disconnected)
                        signOut()
                        delay(300) // Give some time for the disconnection event to be transmitted and valid
                        setLeaderboard(connectionStatus = ConnectionStatus.Off)
                    }
                } else {
                    setLeaderboard(connectionStatus = ConnectionStatus.On)
                }
            }
        }
    }

    fun setSelectedTab(tab: Tab) {
        _selectedTab.value = tab
    }

    fun loadAchievements(onLoaded: () -> Unit) {
        launch {
            userAchievementsOrderedByName = Achievement.from(achievementsDao.getAllOrderedByName())
            userAchievementsOrderedByDone = Achievement.from(achievementsDao.getAllOrderedByCompletionAndName())
            setOrderOption(yourAchievements.value.orderOptions)
        }.invokeOnCompletion {
            onLoaded()
        }
    }

    fun deleteAchievements() {
        launch {
            userAchievementsOrderedByName = userAchievementsOrderedByName.reset()
            userAchievementsOrderedByDone = userAchievementsOrderedByName.reset()
            setOrderOption(yourAchievements.value.orderOptions)
            achievementsDao.resetAll()
        }
    }

    fun setOrderOption(options: OrderOption) {
        setYourAchievements(
            orderOptions = options,
            achievements = when (options) {
                OrderOption.Name -> userAchievementsOrderedByName
                OrderOption.Done -> userAchievementsOrderedByDone
            }
        )
    }

    /**
     * Orders achievements in a coroutine to avoid using UI thread (not doing through SQL even
     * thought it's the most correct because I also want to train with Kotlin
     */
    private fun orderAchievementsDeprecated() {
        launch {
            setYourAchievements(achievements = when (yourAchievements.value.orderOptions) {
                OrderOption.Name -> yourAchievements.value.achievements.toMutableList().apply {
                    sortBy { it.objectName }
                }

                OrderOption.Done -> yourAchievements.value.achievements.toMutableList().apply {
                    sortBy { it.objectName }
                }.let {
                    it.groupBy { if (it.detectionDate != null) 0 else 1 }
                        .toSortedMap()
                        .flatMap { it.value }
                }
            })
        }
    }

    fun signInWithGoogle() {
        launch {
            val userObtained = firebase.signInWithGoogle(application.applicationContext)
            val topUsers = getTopUsers(currUser = userObtained)
            setLeaderboard(
                currUser = userObtained,
                topUsers = topUsers
            )
        }
    }

    fun signOut() {
        launch {
            setLeaderboard(currUser = null)
            firebase.signOut()
        }
    }

    fun submitAchievements(onNoNewAchievements: () -> Unit) {
        launch {
            yourAchievements.value.achievements.let { achiev ->
                if(achiev.isDifferentThan(leaderboard.value.currUser?.achievements)) {
                    val points = achiev.calculatePoints()
                    firebase.updateUserAchievements(achiev.toUserAchievements(), points)
                        .onSuccess {
                            delay(200) // wait a bit for Cloud Functions to process
                            val refreshedTopUsers = firebase.getTopUsers()
                            setLeaderboard(
                                currUser = leaderboard.value.currUser?.copy(
                                    points = points
                                ),
                                topUsers = refreshedTopUsers.getOrNull() ?: leaderboard.value.topUsers
                            )
                        }
                        .onFailure {
                            e("submitAchievements: $it ")
                        }
                } else {
                    i("No new achievements to submit")
                    onNoNewAchievements()
                }
            }
        }
    }

    fun deleteAccount() {
        setLeaderboard(currUser = null)
        firebase.deleteAccount()
    }

    fun refreshLeaderboard() {
        launch {
            val topUsers = getTopUsers(currUser = _leaderboard.value.currUser)
            setLeaderboard(topUsers = topUsers)
        }
    }

    private suspend fun getTopUsers(currUser: User?) : List<User> {
        return if (currUser != null) {
            firebase.getTopUsers().let {
                it.onFailure { e("Error at getTopUsers ${it.message}") }
                it.getOrNull() ?: emptyList()
            }
        } else {
            i("No user logged in to get getTopUsers")
            emptyList()
        }
    }

    /** Util to avoid having to do _yourAchievements.value = ... */
    private fun setYourAchievements(
        achievements: List<Achievement> = yourAchievements.value.achievements,
        orderOptions: OrderOption = yourAchievements.value.orderOptions
    ) {
        _yourAchievements.value = yourAchievements.value.copy(
            achievements = achievements,
            orderOptions = orderOptions
        )
    }

    /** Util to avoid having to do _leaderboard.value = ... */
    private fun setLeaderboard(
        currUser: User? = leaderboard.value.currUser,
        topUsers: List<User> = leaderboard.value.topUsers,
        topObjects: List<ObjectDetectionStats> = leaderboard.value.topObjects,
        connectionStatus: ConnectionStatus = leaderboard.value.connectionStatus
    ) {
        _leaderboard.value = leaderboard.value.copy(
            currUser = currUser,
            topUsers = topUsers,
            topObjects = topObjects,
            connectionStatus = connectionStatus
        )
    }
}
