package com.lifeforge.os.windows

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.lifeforge.os.app.LifeForgeApp
import com.lifeforge.os.core.di.startKoin

fun main() = application {
    startKoin(null)

    val windowState = rememberWindowState(size = DpSize(1280.dp, 800.dp))
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "LifeForge OS",
    ) {
        LifeForgeApp()
    }
}