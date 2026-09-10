package com.lifeforge.os.security

/**
 * iOS placeholder hasher (non-production): use Keychain on iOS later.
 * Deterministic FNV-1a with per-salt encoding keeps within-app verify working.
 */
actual fun platformPinCodeHasher(): PinCodeHasher = IosPinCodeHasher

object IosPinCodeHasher : PinCodeHasher {
    private const val SALT = "lifeforge-pin-v1"

    override fun hash(pin: String): String {
        val input = "$SALT:$pin"
        var h = 0x811C9DC5L
        input.forEach { c ->
            val bytes = c.code
            h = (h xor bytes.toLong()) * 0x1000193L
            h = h and 0xFFFFFFFFL
        }
        return "iosfnv:${h.toULong()}"
    }

    override fun verify(pin: String, hash: String): Boolean {
        if (!hash.startsWith("iosfnv:")) return false
        return hash(pin) == hash
    }
}