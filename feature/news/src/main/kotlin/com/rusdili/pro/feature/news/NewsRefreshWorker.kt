package com.rusdili.pro.feature.news

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rusdili.pro.feature.news.domain.NewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import retrofit2.HttpException

@HiltWorker
class NewsRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val repository: NewsRepository
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result = try {
        repository.refreshWorldNews()
        Result.success()
    } catch (exception: IOException) {
        Result.retry()
    } catch (exception: HttpException) {
        if (exception.code() >= 500) Result.retry() else Result.failure()
    }
}
