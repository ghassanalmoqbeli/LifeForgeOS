package com.lifeforge.os.security

enum class BiometricType { None, Fingerprint, Face, Iris }

interface BiometricAuthenticator {
    fun initialize()
    fun authenticate(title: String, subtitle: String, callback: (Boolean, String?) -> Unit)
    fun isBiometricAvailable(): Boolean
    fun getBiometricType(): BiometricType
}

/**
 * Hash/verify PINs securely. Uses PBKDF2 (or platform secure crypto).
 */
interface PinCodeHasher {
    fun hash(pin: String): String
    fun verify(pin: String, hash: String): Boolean
}