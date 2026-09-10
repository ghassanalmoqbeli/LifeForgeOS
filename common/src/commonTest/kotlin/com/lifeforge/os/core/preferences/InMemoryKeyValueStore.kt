package com.lifeforge.os.core.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [KeyValueStore] for tests.
 */
class InMemoryKeyValueStore : KeyValueStore {
    private val strings = MutableStateFlow<Map<String, String>>(emptyMap())
    private val ints = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val longs = MutableStateFlow<Map<String, Long>>(emptyMap())
    private val floats = MutableStateFlow<Map<String, Float>>(emptyMap())
    private val booleans = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    override fun observeStringNullable(key: String, default: String?): Flow<String?> =
        strings.map { it[key] ?: default }

    override fun observeString(key: String, default: String): Flow<String> =
        strings.map { it[key] ?: default }

    override fun observeInt(key: String, default: Int): Flow<Int> =
        ints.map { it[key] ?: default }

    override fun observeLong(key: String, default: Long): Flow<Long> =
        longs.map { it[key] ?: default }

    override fun observeFloat(key: String, default: Float): Flow<Float> =
        floats.map { it[key] ?: default }

    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        booleans.map { it[key] ?: default }

    override suspend fun putString(key: String, value: String) {
        strings.value = strings.value + (key to value)
    }

    override suspend fun putInt(key: String, value: Int) {
        ints.value = ints.value + (key to value)
    }

    override suspend fun putLong(key: String, value: Long) {
        longs.value = longs.value + (key to value)
    }

    override suspend fun putFloat(key: String, value: Float) {
        floats.value = floats.value + (key to value)
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        booleans.value = booleans.value + (key to value)
    }

    override suspend fun remove(key: String) {
        strings.value = strings.value - key
        ints.value = ints.value - key
        longs.value = longs.value - key
        floats.value = floats.value - key
        booleans.value = booleans.value - key
    }
}