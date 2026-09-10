package com.lifeforge.os.android.backup

import android.app.backup.BackupAgentHelper
import android.app.backup.FileBackupHelper

class LifeForgeBackupAgent : BackupAgentHelper() {

    override fun onCreate() {
        val dbHelper = FileBackupHelper(this, DB_FILE)
        addHelper(DB_HELPER_KEY, dbHelper)
    }

    companion object {
        private const val DB_HELPER_KEY = "lifeforge_db"
        private const val DB_FILE = "databases/LifeForge.db"
    }
}