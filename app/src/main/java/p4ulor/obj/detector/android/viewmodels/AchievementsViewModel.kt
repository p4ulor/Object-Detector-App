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
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.domains.mediapipe.calculatePoints
import p4ulor.obj.detector.data.domains.mediapipe.pointsDifferenceBetween
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
                    if (leaderboard.value.currUser?.isLoggedOut == false){
                        setLeaderboard(connectionStatus = ConnectionStatus.Disconnected)
                        signOut()
                        delay(300) // Give some time for the disconnection event to be transmitted and valid
                    }
                    setLeaderboard(connectionStatus = ConnectionStatus.Off)
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

    /** This is now done through SQL, which is more correct. Keeping it so I recall how it's done in Kotlin */
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

    /** --- FIREBASE --- */

    fun signInWithGoogle() {
        launch {
            val userObtained = firebase.signInWithGoogle(application.applicationContext)
            if (userObtained != null) {
                setLeaderboard(
                    currUser = userObtained,
                    topUsers = getTopUsers(),
                    topObjects = getTopObjects()
                )
            }
        }
    }

    fun signOut() {
        launch {
            setLeaderboard(currUser = leaderboard.value.currUser?.copy()?.setAsLoggedOut()) // copy is used so it produces a different hash (yeah...)
            firebase.signOut()
        }
    }

    fun submitAchievements(onNoNewAchievements: () -> Unit) {
        launch {
            with(yourAchievements.value.achievements) {
                if(pointsDifferenceBetween(leaderboard.value.currUser?.achievements) != 0f) {
                    val newPoints = calculatePoints()
                    val newUserAchievements = toUserAchievements()
                    firebase.updateUserAchievements(newUserAchievements, newPoints)
                        .onSuccess {
                            val topUsers = getTopUsers() // refreshed top users and objects
                            val topObjects = getTopObjects()
                            delay(400) // wait a bit for Cloud Functions to process
                            setLeaderboard(
                                currUser = leaderboard.value.currUser?.copy(
                                    points = newPoints,
                                    achievements = newUserAchievements
                                ),
                                topUsers = topUsers,
                                topObjects = topObjects
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
        setLeaderboard(currUser = leaderboard.value.currUser?.copy()?.setAsLoggedOut()) // copy is used so it produces a different hash (yeah...)
        firebase.deleteAccount()
    }

    fun refreshLeaderboard() {
        launch {
            setLeaderboard(
                topUsers = getTopUsers(),
                topObjects = getTopObjects()
            )
        }
    }

    private suspend fun getTopUsers() : List<User> {
        return firebase.getTopUsers().let {
            it.onFailure { e("Error at getTopUsers ${it.message}") }
            it.getOrNull() ?: emptyList()
        }
    }

    private suspend fun getTopObjects() : List<ObjectDetectionStats> {
        return firebase.getTopObjects().let {
            it.onFailure { e("Error at getTopObjects ${it.message}") }
            it.getOrNull() ?: emptyList()
        }
    }

    /** --- COMMON UTILS --- */

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
