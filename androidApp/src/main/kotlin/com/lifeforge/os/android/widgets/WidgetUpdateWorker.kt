package com.lifeforge.os.android.widgets

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lifeforge.os.core.logging.LifeForgeLogger
import com.lifeforge.os.domain.repository.WaterRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar

class WidgetUpdateWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params), KoinComponent {

    private val waterRepository: WaterRepository by inject()
    private val logger: LifeForgeLogger by inject()

    override suspend fun doWork(): Result {
        return try {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            val totalMl = waterRepository.getTotalForDay(calendar.timeInMillis)
            WidgetViews.updateDailyOverview(applicationContext, totalMl)
            Result.success()
        } catch (e: Exception) {
            logger.error("WidgetUpdateWorker", "Widget update failed", e)
            Result.retry()
        }
    }
}