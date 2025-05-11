package p4ulor.obj.detector.ui.screens.achievements

import p4ulor.obj.detector.data.domains.firebase.ObjectDetectionStats
import p4ulor.obj.detector.data.domains.firebase.User
import p4ulor.obj.detector.data.domains.firebase.UserAchievement
import p4ulor.obj.detector.data.domains.mediapipe.Achievement
import p4ulor.obj.detector.data.utils.ConnectionStatus
import p4ulor.obj.detector.ui.screens.achievements.local.OrderOption

data class YourAchievementsState(
    val achievements: List<Achievement>,
    val orderOptions: OrderOption
)

data class YourAchievementsCallbacks(
    val onDeleteAchievements: () -> Unit,
    val onChangeOrderOption: (OrderOption) -> Unit
)

data class LeaderboardState(
    val currUser: User?,
    val userAchievements: List<UserAchievement>,
    val topUsers: List<User>,
    val topObjects: List<ObjectDetectionStats>,
    val connectionStatus: ConnectionStatus
)

data class LeaderboardSCallbacks(
    val onSignInWithGoogle: () -> Unit,
    val onSignOut: () -> Unit,
    val onSubmitAchievements: () -> Unit,
    val onDeleteAccount: () -> Unit
)
