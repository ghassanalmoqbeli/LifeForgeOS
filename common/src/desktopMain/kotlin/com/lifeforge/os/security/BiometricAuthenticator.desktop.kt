package com.lifeforge.os.security

import com.lifeforge.os.core.logging.LifeForgeLogger

class BiometricAuthenticatorImpl(
    private val logger: LifeForgeLogger,
) : BiometricAuthenticator {

    override fun initialize() {
        // Windows Hello integration would require native code (planned)
    }

    override fun authenticate(
        title: String,
        subtitle: String,
        callback: (Boolean, String?) -> Unit,
    ) {
        logger.info("BiometricAuthenticator", "Desktop biometric not implemented, falling back to PIN")
        callback(false, "Biometric not available on desktop")
    }

    override fun isBiometricAvailable(): Boolean = false

    override fun getBiometricType(): BiometricType = BiometricType.None
}