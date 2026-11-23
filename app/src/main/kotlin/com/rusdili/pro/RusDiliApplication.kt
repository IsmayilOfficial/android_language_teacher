package com.rusdili.pro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rusdili.pro.core.common.UserProgressStore
import com.rusdili.pro.feature.home.DailyReminderScheduler
import com.rusdili.pro.feature.news.NewsRefreshWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class RusDiliApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var progressStore: UserProgressStore
    @Inject lateinit var dailyReminderScheduler: DailyReminderScheduler

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        val request = PeriodicWorkRequestBuilder<NewsRefreshWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "rusdili-world-news",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        appScope.launch {
            val progress = progressStore.progress.first()
            if (progress.reminderEnabled) {
                dailyReminderScheduler.scheduleDaily(
                    hour = progress.reminderHour,
                    minute = progress.reminderMinute,
                    replaceExisting = false
                )
            }
        }
    }
}
