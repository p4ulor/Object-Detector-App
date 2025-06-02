package p4ulor.obj.detector.data.domains.firebase

import p4ulor.obj.detector.data.utils.trimToDecimals

/** Read [User] for more info about Firebase */
data class UserAchievement(
    val objectName: String = "",
    val certaintyScore: Float = 0f,
)

fun List<UserAchievement>.calculatePoints() = fold(initial = 0f) { sum, achiv ->
    sum + achiv.certaintyScore
}.trimToDecimals(decimals = 2)
