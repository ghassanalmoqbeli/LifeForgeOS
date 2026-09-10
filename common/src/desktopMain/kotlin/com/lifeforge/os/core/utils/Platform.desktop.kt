package com.lifeforge.os.core.utils

actual val isDebugBuild: Boolean = System.getenv("LIFEFORGE_DEBUG") == "true" || System.getProperty("lifeforge.debug") == "true"
