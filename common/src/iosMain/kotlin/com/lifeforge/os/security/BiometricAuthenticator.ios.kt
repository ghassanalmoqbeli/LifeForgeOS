package com.lifeforge.os.security

import com.lifeforge.os.core.logging.LifeForgeLogger

class BiometricAuthenticatorImpl(
    private val logger: LifeForgeLogger,
) : BiometricAuthenticator {

    override fun initialize() {
        // LocalAuthentication integration is planned for a later phase.
    }

    override fun authenticate(title: String, subtitle: String, callback: (Boolean, String?) -> Unit) {
        logger.info("BiometricAuthenticator", "iOS biometric not implemented, falling back to PIN")
        callback(false, "Biometric not available")
    }

    override fun isBiometricAvailable(): Boolean = false

    override fun getBiometricType(): BiometricType = BiometricType.None
}