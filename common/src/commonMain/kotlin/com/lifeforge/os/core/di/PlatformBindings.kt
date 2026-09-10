package com.lifeforge.os.core.di

import com.lifeforge.os.core.preferences.KeyValueStore
import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.security.BiometricAuthenticator
import com.lifeforge.os.security.PinCodeHasher
import com.lifeforge.os.sync.cloud.CloudSyncProvider

/**
 * Aggregates platform-specific pieces so commonMain Koin module stays
 * free of platform APIs.
 */
interface PlatformBindings {
    val keyValueStore: KeyValueStore
    val db: LifeForgeDb
    val cloudSyncProvider: CloudSyncProvider?
    val biometricAuthenticator: BiometricAuthenticator
    val pinCodeHasher: PinCodeHasher
}

expect fun createPlatformBindings(context: Any?): PlatformBindings