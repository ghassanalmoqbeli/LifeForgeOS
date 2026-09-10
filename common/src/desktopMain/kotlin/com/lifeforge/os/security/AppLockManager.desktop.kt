package com.lifeforge.os.security

import com.lifeforge.os.core.logging.LifeForgeLogger
import com.lifeforge.os.core.preferences.PreferencesManager
import com.lifeforge.os.core.utils.CoroutineScopeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppLockManagerImpl(
    private val preferences: PreferencesManager,
    private val scopeProvider: CoroutineScopeProvider,
    private val biometric: BiometricAuthenticator,
    private val hasher: PinCodeHasher,
    private val logger: LifeForgeLogger,
) : AppLockManager {

    private val _isLocked = MutableStateFlow(true)
    override val isLocked = _isLocked.asStateFlow()

    private val _lockReason = MutableStateFlow(LockReason.Initial)
    override val lockReason = _lockReason.asStateFlow()

    private var lastActiveTime = System.currentTimeMillis()
    private var cachedPinHash: String? = null
    private var cachedTimeoutMs: Long = 300_000L

    override fun initialize() {
        scopeProvider.ioScope.launch {
            cachedPinHash = preferences.appLockPinHash.first()
            cachedTimeoutMs = preferences.appLockTimeout.first()
            val enabled = preferences.appLockEnabled.first()
            _isLocked.value = enabled
            _lockReason.value = if (enabled) LockReason.Initial else LockReason.Unlocked
        }
    }

    override fun unlock(pin: String): Boolean {
        val storedHash = cachedPinHash
        if (storedHash.isNullOrEmpty()) {
            _isLocked.value = false
            _lockReason.value = LockReason.Unlocked
            return true
        }
        if (hasher.verify(pin, storedHash)) {
            _isLocked.value = false
            _lockReason.value = LockReason.Unlocked
            lastActiveTime = System.currentTimeMillis()
            return true
        }
        return false
    }

    override fun unlockWithBiometric(callback: (Boolean, String?) -> Unit) {
        biometric.authenticate("Unlock LifeForge OS", "Authenticate") { success, error ->
            if (success) {
                _isLocked.value = false
                _lockReason.value = LockReason.Unlocked
                lastActiveTime = System.currentTimeMillis()
            }
            callback(success, error)
        }
    }

    override fun lock(reason: LockReason) {
        _isLocked.value = true
        _lockReason.value = reason
        lastActiveTime = System.currentTimeMillis()
    }

    override fun setPin(newPin: String): Boolean {
        if (newPin.length < 4 || newPin.any { !it.isDigit() }) return false
        val hash = hasher.hash(newPin)
        cachedPinHash = hash
        scopeProvider.ioScope.launch {
            preferences.setAppLockPinHash(hash)
            preferences.setAppLockEnabled(true)
        }
        return true
    }

    override fun changePin(oldPin: String, newPin: String): Boolean {
        val storedHash = cachedPinHash
        if (storedHash.isNullOrEmpty()) return false
        if (!hasher.verify(oldPin, storedHash)) return false
        return setPin(newPin)
    }

    override fun removePin() {
        cachedPinHash = null
        scopeProvider.ioScope.launch {
            preferences.setAppLockEnabled(false)
            preferences.setAppLockPinHash("")
        }
        _isLocked.value = false
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        scopeProvider.ioScope.launch { preferences.setAppLockBiometricEnabled(enabled) }
    }

    override fun setAutoLockTimeout(timeoutMs: Long) {
        cachedTimeoutMs = timeoutMs
        scopeProvider.ioScope.launch { preferences.setAppLockTimeout(timeoutMs) }
    }

    override fun setLockOnBackground(enabled: Boolean) {
        scopeProvider.ioScope.launch { preferences.setAppLockOnBackground(enabled) }
    }
}