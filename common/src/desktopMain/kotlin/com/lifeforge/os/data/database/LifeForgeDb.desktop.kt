package com.lifeforge.os.data.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class LifeForgeDb internal constructor(
    actual val database: com.lifeforge.os.data.database.LifeForgeDatabase,
) {
    actual companion object {
        @Volatile
        private var INSTANCE: LifeForgeDb? = null

        actual fun getInstance(context: Any?): LifeForgeDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val dbFile = File(System.getProperty("user.home"), "LifeForge/LifeForge.db")
                    dbFile.parentFile?.mkdirs()
                    val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
                    if (!dbFile.exists() || dbFile.length() == 0L) {
                        com.lifeforge.os.data.database.LifeForgeDatabase.Schema.create(driver)
                    }
                    LifeForgeDb(
                        com.lifeforge.os.data.database.LifeForgeDatabase(
                            driver = driver,
                        ),
                    )
                }
            }
        }
    }
}