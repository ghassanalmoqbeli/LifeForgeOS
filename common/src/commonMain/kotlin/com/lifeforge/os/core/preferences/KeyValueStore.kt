package com.lifeforge.os.core.preferences

import kotlin.jvm.JvmName
import kotlinx.coroutines.flow.Flow

/**
 * Minimal typed key-value store. Implemented per platform
 * (DataStore on Android, properties/prefs file on Desktop).
 */
interface KeyValueStore {
    @JvmName("observeStringNullable")
    fun observeString(key: String, default: String?): Flow<String?>
    fun observeString(key: String, default: String): Flow<String>
    fun observeInt(key: String, default: Int): Flow<Int>
    fun observeLong(key: String, default: Long): Flow<Long>
    fun observeFloat(key: String, default: Float): Flow<Float>
    fun observeBoolean(key: String, default: Boolean): Flow<Boolean>

    suspend fun putString(key: String, value: String)
    suspend fun putInt(key: String, value: Int)
    suspend fun putLong(key: String, value: Long)
    suspend fun putFloat(key: String, value: Float)
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun remove(key: String)
}