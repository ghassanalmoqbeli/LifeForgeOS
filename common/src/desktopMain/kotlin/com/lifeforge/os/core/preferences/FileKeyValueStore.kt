package com.lifeforge.os.core.preferences

import kotlin.jvm.JvmName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Properties

/**
 * Desktop key-value store backed by a properties file.
 * Keeps an in-memory [MutableStateFlow] mirror so Flows stay reactive on JVM.
 */
class FileKeyValueStore(
    private val file: File,
) : KeyValueStore {

    private val mirror = MutableStateFlow(Properties())
    private val mutex = Mutex()
    private val listeners = mutableMapOf<String, MutableStateFlow<String?>>()

    init {
        file.parentFile?.mkdirs()
        reload()
    }

    private fun reload() {
        val props = Properties()
        if (file.exists()) {
            file.inputStream().use { props.load(it) }
        }
        mirror.value = props
    }

    private suspend fun persist() = mutex.withLock {
        withContext(Dispatchers.IO) {
            mirror.value.store(file.outputStream(), "LifeForge OS Settings")
        }
    }

    private fun node(key: String): MutableStateFlow<String?> =
        listeners.getOrPut(key) { MutableStateFlow(mirror.value.getProperty(key)) }

    private fun set(key: String, value: Any?) {
        val props = Properties().apply {
            mirror.value.forEach { k, v -> setProperty(k as String, v as String) }
            if (value == null) remove(key) else setProperty(key, value.toString())
        }
        mirror.value = props
        listeners[key]?.value = value?.toString()
    }

    @JvmName("observeStringNullable")
    override fun observeString(key: String, default: String?): Flow<String?> =
        node(key).map { it ?: default }

    override fun observeString(key: String, default: String): Flow<String> =
        node(key).map { it ?: default }

    override fun observeInt(key: String, default: Int): Flow<Int> =
        node(key).map { it?.toIntOrNull() ?: default }

    override fun observeLong(key: String, default: Long): Flow<Long> =
        node(key).map { it?.toLongOrNull() ?: default }

    override fun observeFloat(key: String, default: Float): Flow<Float> =
        node(key).map { it?.toFloatOrNull() ?: default }

    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        node(key).map { it?.toBooleanStrictOrNull() ?: default }

    override suspend fun putString(key: String, value: String) { set(key, value); persist() }
    override suspend fun putInt(key: String, value: Int) { set(key, value); persist() }
    override suspend fun putLong(key: String, value: Long) { set(key, value); persist() }
    override suspend fun putFloat(key: String, value: Float) { set(key, value); persist() }
    override suspend fun putBoolean(key: String, value: Boolean) { set(key, value); persist() }
    override suspend fun remove(key: String) { set(key, null); persist() }
}