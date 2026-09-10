plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget()
    jvm("desktop")
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    val commonTest by getting {
        dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Core
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.protobuf)
                implementation(libs.okio)

                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.animation)
                implementation(compose.ui)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)

                // Navigation
                implementation(compose.navigation)
                implementation(compose.navigation.serialization)

                // Koin DI
                implementation(libs.koin.core)
                implementation(libs.koin.compose)

                // SQLDelight
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.android)
                implementation(libs.sqldelight.jvm)

                // Result/Error handling
                implementation(libs.arrow.core)

                // Logging
                implementation(libs.kermit)

                // UUID
                implementation(libs.uuid)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.androidx.datastore.core)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.biometric)
                implementation(libs.androidx.security.crypto)
                implementation(libs.google.play.services.auth)
                implementation(libs.firebase.auth)
                implementation(libs.firebase.firestore)
                implementation(libs.firebase.storage)
                implementation(libs.firebase.messaging)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.compose.desktop.webview)
                implementation(libs.compose.desktop.window)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.slf4j.api)
                implementation(libs.logback.classic)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.ios)
                implementation(libs.sqldelight.jvm)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(compose.ui.test)
                implementation(compose.ui.test.junit4)
                implementation(compose.ui.test.manifest)
            }
        }
    }
}

android {
    namespace = "com.lifeforge.os.common"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    buildFeatures {
        compose = true
    }
    packagingOptions {
        resources.excludes += "META-INF/*"
    }
}

sqldelight {
    databases {
        create("LifeForgeDatabase") {
            packageName.set("com.lifeforge.os.data.database")
        }
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "iosArm64"
    val framework = kotlin.targets.getByName(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}

tasks.named("build") {
    dependsOn(packForXcode)
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries {
            framework {
                baseName = "LifeForgeCommon"
                isStatic = false
            }
        }
    }
}

val composeCompilerVersion = "1.6.10"