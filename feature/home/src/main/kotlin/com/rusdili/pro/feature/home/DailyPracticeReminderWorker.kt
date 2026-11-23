package com.rusdili.pro.feature.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.rusdili.pro.core.database.ReviewSessionDao
import com.rusdili.pro.core.database.WordDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import kotlinx.coroutines.flow.first

@HiltWorker
class DailyPracticeReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val progressStore: UserProgressStore,
    private val wordDao: WordDao,
    private val reviewSessionDao: ReviewSessionDao
) : CoroutineWorker(context, parameters) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val forceShow = inputData.getBoolean(KEY_FORCE_SHOW, false)
        val progress = progressStore.progress.first()

        if (!forceShow && !progress.reminderEnabled) {
            return Result.success()
        }

        val now = System.currentTimeMillis()
        val dueWords = wordDao.dueForReview(now, 100)
        val recentSessions = reviewSessionDao.observeAll().first()
        val completedReviewToday = recentSessions.any { isSameDay(it.completedAt, now) }

        if (!forceShow &&
            progress.isTodayActive &&
            progress.xpToday >= AppConstants.DAILY_GOAL_XP &&
            completedReviewToday &&
            dueWords.isEmpty()
        ) {
            return Result.success()
        }
        if (!hasNotificationPermission(applicationContext)) {
            return Result.success()
        }

        ensureNotificationChannel(applicationContext)

        val remainingXp = (AppConstants.DAILY_GOAL_XP - progress.xpToday).coerceAtLeast(0)
        val (title, body) = when {
            dueWords.isNotEmpty() && !completedReviewToday ->
                "Təkrar sessiyası gözləyir! (${dueWords.size} söz)" to
                    "Bu gün təkrar edilməli ${dueWords.size} rusca sözün var. Qısa təkrar sessiyası ilə yaddaşını möhkəmləndir!"
            !completedReviewToday && progress.streak > 0 ->
                "${progress.streak} günlük seriyanı qoru!" to
                    "Bugünkü dil təkrar sessiyanı hələ tamamlamamısan. Qısa test və ya kartlarla məşqə başla!"
            !completedReviewToday || progress.xpToday < AppConstants.DAILY_GOAL_XP ->
                "Gündəlik dil təkrarı vaxtıdır!" to
                    "Bugünkü hədəfə çatmaq üçün $remainingXp XP qalıb. Təkrar sessiyasına başla və irəliləyişini artır!"
            else ->
                "LL - Learn Language · Təkrar xatırlatması" to
                    "Gündəlik xatırlatma saat %02d:%02d üçün aktivdir. Bugünkü ${progress.xpToday} XP və təkrar sessiyan tamamlanıb!"
                        .format(progress.reminderHour, progress.reminderMinute)
        }

        val launchIntent = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(AppConstants.EXTRA_NOTIFICATION_DESTINATION, AppConstants.DESTINATION_PRACTICE)
            }
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

    private fun isSameDay(timestampA: Long, timestampB: Long): Boolean {
        val calA = Calendar.getInstance().apply { timeInMillis = timestampA }
        val calB = Calendar.getInstance().apply { timeInMillis = timestampB }
        return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
            calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
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
                description = "Gündəlik rus dili təkrar sessiyaları və seriyanı qorumaq üçün xatırlatmalar"
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
