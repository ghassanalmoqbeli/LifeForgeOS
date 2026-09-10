package com.lifeforge.os.core.utils

import java.util.UUID

actual fun randomUuid(): String = UUID.randomUUID().toString()