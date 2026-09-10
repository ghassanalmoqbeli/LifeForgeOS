package com.lifeforge.os.core.logging

import com.lifeforge.os.core.utils.isDebugBuild

interface LifeForgeLogger {
    fun init(context: Any?)
    fun debug(tag: String, message: String, throwable: Throwable? = null)
    fun info(tag: String, message: String, throwable: Throwable? = null)
    fun warning(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
    fun wtf(tag: String, message: String, throwable: Throwable? = null)
}

class LifeForgeLoggerImpl : LifeForgeLogger {
    private var initialized = false
    private val logLevel = if (isDebugBuild) LogLevel.DEBUG else LogLevel.INFO

    override fun init(context: Any?) {
        initialized = true
    }

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        if (logLevel.ordinal <= LogLevel.DEBUG.ordinal) log(LogLevel.DEBUG, tag, message, throwable)
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        if (logLevel.ordinal <= LogLevel.INFO.ordinal) log(LogLevel.INFO, tag, message, throwable)
    }

    override fun warning(tag: String, message: String, throwable: Throwable?) {
        if (logLevel.ordinal <= LogLevel.WARNING.ordinal) log(LogLevel.WARNING, tag, message, throwable)
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (logLevel.ordinal <= LogLevel.ERROR.ordinal) log(LogLevel.ERROR, tag, message, throwable)
    }

    override fun wtf(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.WTF, tag, message, throwable)
    }

    private fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        val formatted = "[$tag] $message"
        when (level) {
            LogLevel.DEBUG -> println("DEBUG: $formatted")
            LogLevel.INFO -> println("INFO: $formatted")
            LogLevel.WARNING -> println("WARN: $formatted")
            LogLevel.ERROR -> System.err.println("ERROR: $formatted")
            LogLevel.WTF -> System.err.println("WTF: $formatted")
        }
        throwable?.printStackTrace()
    }
}

enum class LogLevel { DEBUG, INFO, WARNING, ERROR, WTF }