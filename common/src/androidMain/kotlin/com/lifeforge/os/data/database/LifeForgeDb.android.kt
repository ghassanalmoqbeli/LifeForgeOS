package com.lifeforge.os.data.database

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver

actual class LifeForgeDb private constructor(
    actual val database: com.lifeforge.os.data.database.LifeForgeDatabase,
) {
    actual companion object {
        @Volatile
        private var INSTANCE: LifeForgeDb? = null

        actual fun getInstance(context: Any?): LifeForgeDb {
            val ctx = context as Context
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LifeForgeDb(
                    com.lifeforge.os.data.database.LifeForgeDatabase(
                        driver = AndroidSqliteDriver(
                            com.lifeforge.os.data.database.LifeForgeDatabase.Schema,
                            ctx,
                            "LifeForge.db",
                        ),
                    ),
                )
            }
        }
    }
}