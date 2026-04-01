package com.daniebeler.pfpixelix.utils

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

fun configureJavaLogger(isDebug: Boolean = false) {
    configureLogger(isDebug)
    Logger.addLogWriter(Slf4jLogWriter)
}

private object Slf4jLogWriter : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val logger = LoggerFactory.getLogger(tag).atLevel(severity.slf4jLevel)

        if (throwable != null) {
            logger.setCause(throwable)
        }

        logger.log(message)
    }

    private val Severity.slf4jLevel: Level
        get() = when (this) {
            Severity.Verbose -> Level.TRACE
            Severity.Debug -> Level.DEBUG
            Severity.Info -> Level.INFO
            Severity.Warn -> Level.WARN
            Severity.Error -> Level.ERROR
            Severity.Assert -> Level.ERROR
        }
}