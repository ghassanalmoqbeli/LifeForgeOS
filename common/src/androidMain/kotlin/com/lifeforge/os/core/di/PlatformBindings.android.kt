package com.lifeforge.os.core.di

import android.content.Context
import com.lifeforge.os.core.logging.LifeForgeLoggerImpl
import com.lifeforge.os.core.preferences.DataStoreKeyValueStore
import com.lifeforge.os.core.preferences.KeyValueStore
import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.security.BiometricAuthenticator
import com.lifeforge.os.security.BiometricAuthenticatorImpl
import com.lifeforge.os.security.PinCodeHasher
import com.lifeforge.os.security.platformPinCodeHasher
import com.lifeforge.os.sync.cloud.CloudSyncProvider
import com.lifeforge.os.sync.cloud.FirebaseSyncProvider

actual fun createPlatformBindings(context: Any?): PlatformBindings {
    val ctx = context as Context
    val logger = LifeForgeLoggerImpl()
    return object : PlatformBindings {
        override val keyValueStore: KeyValueStore = DataStoreKeyValueStore(ctx)
        override val db: LifeForgeDb = LifeForgeDb.getInstance(ctx)
        override val cloudSyncProvider: CloudSyncProvider? = FirebaseSyncProvider(ctx, logger)
        override val biometricAuthenticator: BiometricAuthenticator = BiometricAuthenticatorImpl(ctx, logger)
        override val pinCodeHasher: PinCodeHasher = platformPinCodeHasher()
    }
}