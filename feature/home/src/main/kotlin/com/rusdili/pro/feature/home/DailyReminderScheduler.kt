package com.rusdili.pro.feature.home

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleDaily(
        hour: Int,
        minute: Int,
        replaceExisting: Boolean = true
    ) {
        val delayMillis = computeInitialDelayMillis(hour, minute)
        val request = PeriodicWorkRequestBuilder<DailyPracticeReminderWorker>(
            24,
            TimeUnit.HOURS
        )
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .addTag(UNIQUE_WORK_NAME)
            .build()

        val policy = if (replaceExisting) {
            ExistingPeriodicWorkPolicy.UPDATE
        } else {
            ExistingPeriodicWorkPolicy.KEEP
        }

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            policy,
            request
        )
    }

    fun cancelDaily() {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    fun sendTestNotificationNow() {
        val request = OneTimeWorkRequestBuilder<DailyPracticeReminderWorker>()
            .setInputData(workDataOf(DailyPracticeReminderWorker.KEY_FORCE_SHOW to true))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            TEST_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    internal fun computeInitialDelayMillis(
        hour: Int,
        minute: Int,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        val now = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val target = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            set(Calendar.MINUTE, minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return (target.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
    }

    companion object {
        const val UNIQUE_WORK_NAME = "ll_daily_practice_reminder"
        const val TEST_WORK_NAME = "ll_daily_practice_reminder_test"
    }
}
