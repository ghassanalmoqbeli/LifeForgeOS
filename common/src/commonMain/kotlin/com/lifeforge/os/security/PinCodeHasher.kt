package com.lifeforge.os.security

// Platform-provided PIN hasher (PBKDF2 on JVM, SecureEnclave/Keychain on iOS later).
expect fun platformPinCodeHasher(): PinCodeHasher

class DefaultPinCodeHasher(private val delegate: () -> PinCodeHasher) : PinCodeHasher {
    override fun hash(pin: String): String = delegate().hash(pin)
    override fun verify(pin: String, hash: String): Boolean = delegate().verify(pin, hash)
}