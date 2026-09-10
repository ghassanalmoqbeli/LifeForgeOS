package com.lifeforge.os.android.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class LifeForgeWidgetReceiver : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { id ->
            val views = WidgetViews.buildDailyOverviewForUpdate(context)
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}