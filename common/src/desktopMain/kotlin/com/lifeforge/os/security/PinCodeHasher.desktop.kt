package com.lifeforge.os.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

actual fun platformPinCodeHasher(): PinCodeHasher = Pbkdf2PinCodeHasher

object Pbkdf2PinCodeHasher : PinCodeHasher {
    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH = 256
    private const val SALT_SIZE = 16
    private val secureRandom = SecureRandom()

    override fun hash(pin: String): String {
        val salt = ByteArray(SALT_SIZE).also { secureRandom.nextBytes(it) }
        val key = derive(pin, salt)
        val saltHex = salt.joinToString("") { "%02x".format(it) }
        val keyHex = key.joinToString("") { "%02x".format(it) }
        return "pbkdf2:$ITERATIONS:$saltHex:$keyHex"
    }

    override fun verify(pin: String, hash: String): Boolean {
        val parts = hash.split(":")
        if (parts.size != 4 || parts[0] != "pbkdf2") return false
        val iterations = parts[1].toIntOrNull() ?: return false
        val salt = hexToBytes(parts[2]) ?: return false
        val expected = hexToBytes(parts[3]) ?: return false
        val actual = deriveWith(pin, salt, iterations)
        return MessageDigest.isEqual(expected, actual)
    }

    private fun derive(pin: String, salt: ByteArray): ByteArray = deriveWith(pin, salt, ITERATIONS)

    private fun deriveWith(pin: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, iterations, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    private fun hexToBytes(hex: String): ByteArray? =
        if (hex.length % 2 != 0) null else ByteArray(hex.length / 2) { i ->
            hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
}