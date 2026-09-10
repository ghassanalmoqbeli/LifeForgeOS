package com.lifeforge.os.data.database

expect class LifeForgeDb {
    companion object {
        fun getInstance(context: Any?): LifeForgeDb
    }

    val database: com.lifeforge.os.data.database.LifeForgeDatabase
}