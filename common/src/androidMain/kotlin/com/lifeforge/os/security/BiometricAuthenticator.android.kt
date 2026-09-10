package com.lifeforge.os.security

import android.app.Activity
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.lifeforge.os.core.logging.LifeForgeLogger

class BiometricAuthenticatorImpl(
    private val context: Context,
    private val logger: LifeForgeLogger,
) : BiometricAuthenticator {

    private var activityProvider: (() -> Activity)? = null

    fun attachActivity(provider: () -> Activity) {
        activityProvider = provider
    }

    override fun initialize() {
        // No-op on Android; Activity attachment is optional per-call
    }

    override fun authenticate(
        title: String,
        subtitle: String,
        callback: (Boolean, String?) -> Unit,
    ) {
        val activity = activityProvider?.invoke()
        if (activity == null) {
            callback(false, "No activity available for biometric prompt")
            return
        }
        if (!isBiometricAvailable()) {
            callback(false, "Biometric not available")
            return
        }

        val executor = ContextCompat.getMainExecutor(context)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    callback(true, null)
                }

                override fun onAuthenticationFailed() {
                    callback(false, "Authentication failed")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    callback(false, errString.toString())
                }
            },
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Use PIN")
            .setAllowedAuthenticators(
                BiometricPrompt.Authenticator.BIOMETRIC_STRONG or
                    BiometricPrompt.Authenticator.DEVICE_CREDENTIAL,
            )
            .build()

        prompt.authenticate(promptInfo)
    }

    override fun isBiometricAvailable(): Boolean {
        val manager = context.getSystemService(Context.BIOMETRIC_SERVICE) as BiometricManager
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    override fun getBiometricType(): BiometricType {
        val manager = context.getSystemService(Context.BIOMETRIC_SERVICE) as BiometricManager
        return if (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
        ) {
            BiometricType.Fingerprint
        } else {
            BiometricType.None
        }
    }
}