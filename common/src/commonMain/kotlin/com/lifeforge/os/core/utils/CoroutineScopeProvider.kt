package com.lifeforge.os.core.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface CoroutineScopeProvider {
    val mainScope: CoroutineScope
    val ioScope: CoroutineScope
    val defaultScope: CoroutineScope
}

class DefaultCoroutineScopeProvider(
    mainDispatcher: CoroutineDispatcher,
) : CoroutineScopeProvider {
    override val mainScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    override val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override val defaultScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}

// Each platform supplies its main-thread dispatcher:
//  - Android uses Dispatchers.Main (kotlinx-coroutines-android)
//  - Desktop uses Dispatchers.Swing (kotlinx-coroutines-swing)
expect fun mainDispatcher(): CoroutineDispatcher