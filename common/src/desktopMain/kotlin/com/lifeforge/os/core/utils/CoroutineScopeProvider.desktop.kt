package com.lifeforge.os.core.utils

import kotlinx.coroutines.CoroutineDispatcher

actual fun mainDispatcher(): CoroutineDispatcher =
    kotlinx.coroutines.Dispatchers.Main