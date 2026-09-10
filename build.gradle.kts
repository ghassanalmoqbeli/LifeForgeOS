plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.0.0" apply false
    id("com.android.application") version "8.4.0" apply false
    id("com.android.library") version "8.4.0" apply false
    id("org.jetbrains.compose") version "1.6.10" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
    id("app.cash.sqldelight") version "2.0.1" apply false
}

allprojects {
    group = "com.lifeforge.os"
    version = "1.0.0-SNAPSHOT"
}