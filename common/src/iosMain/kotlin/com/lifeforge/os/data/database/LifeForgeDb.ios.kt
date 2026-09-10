package com.lifeforge.os.data.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class LifeForgeDb internal constructor(
    override val database: com.lifeforge.os.data.database.LifeForgeDatabase,
) {
    actual companion object {
        @Volatile
        private var INSTANCE: LifeForgeDb? = null

        actual fun getInstance(context: Any?): LifeForgeDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LifeForgeDb(
                    com.lifeforge.os.data.database.LifeForgeDatabase(
                        driver = NativeSqliteDriver(
                            com.lifeforge.os.data.database.LifeForgeDatabase.Schema,
                            "LifeForge.db",
                        ),
                    ),
                )
            }
        }
    }
}