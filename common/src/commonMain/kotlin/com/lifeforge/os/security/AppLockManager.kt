package com.lifeforge.os.security

import kotlinx.coroutines.flow.StateFlow

enum class LockReason { Initial, Unlocked, Timeout, Background, Manual }

interface AppLockManager {
    val isLocked: StateFlow<Boolean>
    val lockReason: StateFlow<LockReason>

    fun initialize()
    fun unlock(pin: String): Boolean
    fun unlockWithBiometric(callback: (Boolean, String?) -> Unit)
    fun lock(reason: LockReason)
    fun setPin(newPin: String): Boolean
    fun changePin(oldPin: String, newPin: String): Boolean
    fun removePin()
    fun setBiometricEnabled(enabled: Boolean)
    fun setAutoLockTimeout(timeoutMs: Long)
    fun setLockOnBackground(enabled: Boolean)
}