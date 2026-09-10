package com.lifeforge.os.android.widgets

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.lifeforge.os.core.logging.LifeForgeLogger
import com.lifeforge.os.domain.repository.WaterRepository
import org.koin.android.ext.android.inject
import java.util.Calendar

class WidgetUpdateWorker(
    appContext: Context,
    params: WorkerParameters,
) : Worker(appContext, params) {

    private val waterRepository: WaterRepository by appContext.inject()
    private val logger: LifeForgeLogger by appContext.inject()

    override fun doWork(): Result {
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