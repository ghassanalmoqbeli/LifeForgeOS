package com.lifeforge.os.core.utils

import platform.Foundation.NSUUID

actual fun randomUuid(): String = NSUUID().UUIDString