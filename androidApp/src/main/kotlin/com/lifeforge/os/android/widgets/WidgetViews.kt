package com.lifeforge.os.android.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.lifeforge.os.android.R

object WidgetViews {

    fun updateDailyOverview(context: Context, waterMl: Int) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, LifeForgeWidgetReceiver::class.java)
        )
        ids.forEach { id ->
            val views = buildDailyOverview(context, waterMl)
            manager.updateAppWidget(id, views)
        }
    }

    fun buildDailyOverviewForUpdate(context: Context): RemoteViews =
        buildDailyOverview(context, -1)

    private fun buildDailyOverview(context: Context, waterMl: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_daily_overview)
        views.setTextViewText(R.id.widget_title, context.getString(R.string.widget_daily_overview))
        val waterLabel = if (waterMl >= 0) {
            context.getString(R.string.widget_water_value, waterMl)
        } else {
            context.getString(R.string.widget_empty)
        }
        views.setTextViewText(R.id.widget_water, waterLabel)
        return views
    }
}