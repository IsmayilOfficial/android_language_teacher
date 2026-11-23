package com.rusdili.pro.feature.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rusdili.pro.core.common.AppConstants
import com.rusdili.pro.core.common.UserProgressStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DailyPracticeReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val progressStore: UserProgressStore
) : CoroutineWorker(context, parameters) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val forceShow = inputData.getBoolean(KEY_FORCE_SHOW, false)
        val progress = progressStore.progress.first()

        if (!forceShow && !progress.reminderEnabled) {
            return Result.success()
        }
        if (!forceShow && progress.isTodayActive && progress.xpToday >= AppConstants.DAILY_GOAL_XP) {
            return Result.success()
        }
        if (!hasNotificationPermission(applicationContext)) {
            return Result.success()
        }

        ensureNotificationChannel(applicationContext)

        val remainingXp = (AppConstants.DAILY_GOAL_XP - progress.xpToday).coerceAtLeast(0)
        val (title, body) = when {
            !progress.isTodayActive && progress.streak > 0 ->
                "${progress.streak} günlük seriyanı qoru!" to
                    "Bu gün hələ məşq etməmisən. Qısa rus dili dərsi ilə seriyanı davam etdir."
            progress.xpToday < AppConstants.DAILY_GOAL_XP ->
                "Rus dili məşqi vaxtıdır!" to
                    "Bugünkü hədəfə çatmaq üçün $remainingXp XP qalıb. İndi davam et!"
            else ->
                "LL - Learn Language" to
                    "Gündəlik xatırlatma aktivdir! Bugünkü hədəfini (${progress.xpToday} XP) tamamlamısan."
        }

        val launchIntent = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)
        val pendingIntent = launchIntent?.let {
            PendingIntent.getActivity(
                applicationContext,
                NOTIFICATION_ID,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_practice)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .apply {
                if (pendingIntent != null) setContentIntent(pendingIntent)
            }
            .build()

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        return Result.success()
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        val runtimeGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        return runtimeGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Gündəlik məşq xatırlatması",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gündəlik rus dili dərsləri və seriyanı qorumaq üçün xatırlatmalar"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "daily_practice_reminder"
        const val NOTIFICATION_ID = 1001
        const val KEY_FORCE_SHOW = "force_show"
    }
}
