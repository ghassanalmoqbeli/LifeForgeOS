package com.lifeforge.os.core.di

import com.lifeforge.os.core.logging.LifeForgeLoggerImpl
import com.lifeforge.os.core.preferences.FileKeyValueStore
import com.lifeforge.os.core.preferences.KeyValueStore
import com.lifeforge.os.core.preferences.PreferencesManagerImpl
import com.lifeforge.os.core.utils.DefaultCoroutineScopeProvider
import com.lifeforge.os.core.utils.mainDispatcher
import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.security.AppLockManager
import com.lifeforge.os.security.AppLockManagerImpl
import com.lifeforge.os.security.BiometricAuthenticator
import com.lifeforge.os.security.BiometricAuthenticatorImpl
import com.lifeforge.os.security.PinCodeHasher
import com.lifeforge.os.security.platformPinCodeHasher
import com.lifeforge.os.sync.cloud.CloudSyncProvider
import java.io.File

actual fun createPlatformBindings(context: Any?): PlatformBindings {
    val logger = LifeForgeLoggerImpl()
    val home = File(System.getProperty("user.home"), "LifeForge")
    return object : PlatformBindings {
        override val keyValueStore: KeyValueStore = FileKeyValueStore(File(home, "LifeForge.properties"))
        override val db: LifeForgeDb = LifeForgeDb.getInstance(null)
        override val cloudSyncProvider: CloudSyncProvider? = null
        override val biometricAuthenticator: BiometricAuthenticator = BiometricAuthenticatorImpl(logger)
        override val pinCodeHasher: PinCodeHasher = platformPinCodeHasher()
        override val appLockManager: AppLockManager =
            AppLockManagerImpl(PreferencesManagerImpl(keyValueStore), DefaultCoroutineScopeProvider(mainDispatcher()), biometricAuthenticator, pinCodeHasher, logger)
    }
}