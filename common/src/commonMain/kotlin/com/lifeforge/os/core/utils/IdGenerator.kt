package com.lifeforge.os.core.utils

import kotlin.random.Random

fun newEntityId(): String =
    "${System.currentTimeMillis()}-${Random.nextLong().toString().substring(1, 9)}"