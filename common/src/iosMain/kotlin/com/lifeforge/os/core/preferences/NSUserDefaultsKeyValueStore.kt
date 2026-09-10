package com.lifeforge.os.core.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import platform.Foundation.NSUserDefaults

/**
 * iOS key-value store: write-through persistence via NSUserDefaults with an
 * in-memory reactive mirror (mirrors the Desktop FileKeyValueStore pattern).
 */
class NSUserDefaultsKeyValueStore(
    private val suiteName: String = "LifeForge",
) : KeyValueStore {

    private val defaults: NSUserDefaults = (NSUserDefaults(suiteName = suiteName)) ?: NSUserDefaults.standardUserDefaults

    private val strings = MutableStateFlow<Map<String, String>>(loadStrings())
    private val longs = MutableStateFlow<Map<String, Long>>(loadLongs())

    private fun loadStrings(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val dict = defaults.dictionaryRepresentation()
        dict.forEach { key, value ->
            (value as? String)?.let { result[key] = it }
        }
        return result
    }

    private fun loadLongs(): Map<String, Long> {
        val result = mutableMapOf<String, Long>()
        val dict = defaults.dictionaryRepresentation()
        dict.forEach { key, value ->
            (value as? Number)?.let { result[key] = it.longValue }
        }
        return result
    }

    override fun observeString(key: String, default: String?): Flow<String?> =
        strings.map { it[key] ?: default }

    override fun observeString(key: String, default: String): Flow<String> =
        strings.map { it[key] ?: default }

    override fun observeInt(key: String, default: Int): Flow<Int> =
        longs.map { (it[key] ?: default.toLong()).toInt() }

    override fun observeLong(key: String, default: Long): Flow<Long> =
        longs.map { it[key] ?: default }

    override fun observeFloat(key: String, default: Float): Flow<Float> =
        longs.map { (it[key] ?: default.toLong()).toFloat() }

    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        longs.map { (it[key] ?: if (default) 1L else 0L) != 0L }

    override suspend fun putString(key: String, value: String) {
        strings.value = strings.value + (key to value)
        defaults.setObject(value, forKey = key)
    }

    override suspend fun putInt(key: String, value: Int) = putLong(key, value.toLong())

    override suspend fun putLong(key: String, value: Long) {
        longs.value = longs.value + (key to value)
        defaults.setInteger(value, forKey = key)
    }

    override suspend fun putFloat(key: String, value: Float) = putLong(key, value.toLong())

    override suspend fun putBoolean(key: String, value: Boolean) = putLong(key, if (value) 1L else 0L)

    override suspend fun remove(key: String) {
        strings.value = strings.value - key
        longs.value = longs.value - key
        defaults.removeObjectForKey(key)
    }
}