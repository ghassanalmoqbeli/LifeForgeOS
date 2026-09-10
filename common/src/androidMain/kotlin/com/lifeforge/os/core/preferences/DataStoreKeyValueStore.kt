package com.lifeforge.os.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlin.jvm.JvmName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lifeforge_prefs")

class DataStoreKeyValueStore(private val context: Context) : KeyValueStore {

    @JvmName("observeStringNullable")
    override fun observeString(key: String, default: String?): Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[stringPreferencesKey(key)] ?: default }

    override fun observeString(key: String, default: String): Flow<String> =
        context.dataStore.data.map { prefs -> prefs[stringPreferencesKey(key)] ?: default }

    override fun observeInt(key: String, default: Int): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[intPreferencesKey(key)] ?: default }

    override fun observeLong(key: String, default: Long): Flow<Long> =
        context.dataStore.data.map { prefs -> prefs[longPreferencesKey(key)] ?: default }

    override fun observeFloat(key: String, default: Float): Flow<Float> =
        context.dataStore.data.map { prefs -> prefs[floatPreferencesKey(key)] ?: default }

    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[booleanPreferencesKey(key)] ?: default }

    override suspend fun putString(key: String, value: String) {
        context.dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    override suspend fun putInt(key: String, value: Int) {
        context.dataStore.edit { it[intPreferencesKey(key)] = value }
    }

    override suspend fun putLong(key: String, value: Long) {
        context.dataStore.edit { it[longPreferencesKey(key)] = value }
    }

    override suspend fun putFloat(key: String, value: Float) {
        context.dataStore.edit { it[floatPreferencesKey(key)] = value }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        context.dataStore.edit { it[booleanPreferencesKey(key)] = value }
    }

    override suspend fun remove(key: String) {
        context.dataStore.edit { it.remove(stringPreferencesKey(key)) }
    }
}