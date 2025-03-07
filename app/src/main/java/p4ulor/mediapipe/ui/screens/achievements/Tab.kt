package p4ulor.mediapipe.ui.screens.achievements

import androidx.annotation.StringRes
import p4ulor.mediapipe.R

enum class Tab(@StringRes val label: Int) {
    YourAchievements(R.string.your_achievements),
    Leaderboard(R.string.leaderboard)
}
