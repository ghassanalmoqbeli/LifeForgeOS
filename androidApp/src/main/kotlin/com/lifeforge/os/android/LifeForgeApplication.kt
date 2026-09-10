package com.lifeforge.os.android

import android.app.Application
import android.content.Context
import com.lifeforge.os.core.di.startKoin
import com.lifeforge.os.core.logging.LifeForgeLogger
import com.lifeforge.os.security.AppLockManager
import com.lifeforge.os.sync.SyncManager
import org.koin.android.ext.android.inject

class LifeForgeApplication : Application() {

    private val appLockManager: AppLockManager by inject()
    private val syncManager: SyncManager by inject()
    private val logger: LifeForgeLogger by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin(this)

        logger.init(this)

        appLockManager.initialize()
        syncManager.initialize(this)

        logger.info("LifeForgeApplication", "Application initialized")
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun get(context: Context): LifeForgeApplication = context.applicationContext as LifeForgeApplication
    }
}