package com.snowdango.sumire.repository.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.snowdango.sumire.data.entity.db.Histories

/**
 * version 3 → 4: 履歴に再生時間 (listening_ms) を追加する。
 * それまでの履歴は再生時間を測っていないので、0 ではなく null (未計測) のままにする
 */
internal val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE `${Histories.TABLE_NAME}` ADD COLUMN `${Histories.COLUMN_LISTENING_MS}` INTEGER",
        )
    }
}
