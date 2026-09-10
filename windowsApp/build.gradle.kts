import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            kotlin.srcDir("src/main/kotlin")

            dependencies {
                implementation(project(":common"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.slf4j.api)
                implementation(libs.logback.classic)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.lifeforge.os.windows.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "LifeForge OS"
            packageVersion = "1.0.0"
            description = "Premium Personal Life OS for Windows & Android"
            vendor = "LifeForge"
            licenseFile.set(project.file("LICENSE.txt"))

            windows {
                menuGroup = "LifeForge"
                shortcut = true
                dirChooser = true
                console = false
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
        )
    }
}