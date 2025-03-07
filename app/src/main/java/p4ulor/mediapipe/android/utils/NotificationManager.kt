package p4ulor.mediapipe.android.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.koin.core.annotation.Single
import p4ulor.mediapipe.R
import p4ulor.mediapipe.android.activities.MainActivity

@Single
class NotificationManager(private val ctx: Context) {
    private var notificationManager = NotificationManagerCompat.from(ctx)

    /** The one and only channel for Achievements */
    private val notificationChannel = NotificationChannel(
        /* id = */ notificationChannelId,
        /* name = */ ctx.getString(R.string.achievements),
        /* importance = */ NotificationManager.IMPORTANCE_DEFAULT
    )

    init {
        // Register the channel with the OS
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @SuppressLint("MissingPermission") // Because IDE isn't recognizing the hasPermission check
    fun sendAchievementNotification(title: String, description: String){
        // Explicit intent for MainActivity
        val pendingIntent = PendingIntent.getActivity(
            /* context = */ ctx,
            /* requestCode = */ 0,
            /* intent = */ Intent(ctx, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            /* flags = */ PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(ctx, notificationChannelId)
            .setSmallIcon(R.mipmap.app_icon_round)
            .setLargeIcon(ctx.getBitmapFor(R.mipmap.app_icon_round))
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ctx hasPermission Manifest.permission.POST_NOTIFICATIONS) {
            notificationManager.notify(200, notification)
        }
    }

    private companion object {
        /** The same ID is used for all notification so new ones update old ones that weren't opened */
        const val notificationChannelId = "300"
        const val notificationId = 200
    }
}



