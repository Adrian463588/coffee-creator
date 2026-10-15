package com.coffeecreator.app.core.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.coffeecreator.app.core.model.DocumentChunk

@Entity(tableName = "records", primaryKeys = ["kind", "id"])
data class StoredRecord(val kind: String, val id: String, val json: String)

@Entity(tableName = "document_chunks", indices = [Index("documentId")])
data class StoredChunk(@PrimaryKey val id: String, val documentId: String, val content: String, val json: String)

@Fts4(tokenizer = FtsOptions.TOKENIZER_UNICODE61, notIndexed = ["chunkId"])
@Entity(tableName = "chunk_fts")
data class ChunkFts(val chunkId: String, val content: String)

@Dao
interface CoffeeDao {
    @Query("SELECT * FROM records ORDER BY kind, id")
    suspend fun allRecords(): List<StoredRecord>

    @Query("SELECT * FROM document_chunks ORDER BY documentId, id")
    suspend fun allChunks(): List<StoredChunk>

    @Query("SELECT * FROM records WHERE kind = :kind AND id = :id LIMIT 1")
    suspend fun record(kind: String, id: String): StoredRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putRecords(records: List<StoredRecord>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun putChunks(chunks: List<StoredChunk>)

    @Insert
    suspend fun indexChunks(chunks: List<ChunkFts>)

    @Query("DELETE FROM records WHERE kind = :kind AND id = :id")
    suspend fun deleteRecord(kind: String, id: String)

    @Query("DELETE FROM records WHERE kind IN (:kinds)")
    suspend fun deleteKinds(kinds: List<String>)

    @Query("DELETE FROM records WHERE kind = 'setting' AND id IN (:keys)")
    suspend fun deleteSettings(keys: List<String>)

    @Query("DELETE FROM records WHERE kind = 'setting' AND substr(id, 1, length(:prefix)) = :prefix")
    suspend fun deleteSettingsWithPrefix(prefix: String)

    @Query("DELETE FROM chunk_fts WHERE chunkId IN (SELECT id FROM document_chunks WHERE documentId = :documentId)")
    suspend fun removeDocumentIndex(documentId: String)

    @Query("DELETE FROM document_chunks WHERE documentId = :documentId")
    suspend fun removeDocumentChunks(documentId: String)

    @Query("DELETE FROM chunk_fts")
    suspend fun clearIndex()

    @Query("DELETE FROM document_chunks")
    suspend fun clearChunks()

    @Query("SELECT c.* FROM document_chunks c JOIN chunk_fts f ON c.id = f.chunkId WHERE chunk_fts MATCH :matchQuery LIMIT :limit")
    suspend fun search(matchQuery: String, limit: Int): List<StoredChunk>
}

@Database(entities = [StoredRecord::class, StoredChunk::class, ChunkFts::class], version = 2, exportSchema = true)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun dao(): CoffeeDao

    companion object {
        /** Version 1's durable records are retained; version 2 adds real chunk storage and FTS. */
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `document_chunks` (`id` TEXT NOT NULL, `documentId` TEXT NOT " +
                        "NULL, `content` TEXT NOT NULL, `json` TEXT NOT NULL, PRIMARY KEY(`id`))"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_document_chunks_documentId` ON `document_chunks` (`documentId`)")
                db.execSQL(
                    "CREATE VIRTUAL TABLE IF NOT EXISTS `chunk_fts` USING FTS4(`chunkId` TEXT NOT NULL, " +
                        "`content` TEXT NOT NULL, notindexed=`chunkId`, tokenize=unicode61)"
                )
                db.query("SELECT id, json FROM records WHERE kind = 'chunk'").use { cursor ->
                    while (cursor.moveToNext()) {
                        val json = cursor.getString(1)
                        val chunk = RecordJson.gson.fromJson(json, DocumentChunk::class.java)
                        require(chunk.id == cursor.getString(0) && chunk.content.isNotBlank()) {
                            "Stored chunk is corrupt; migration aborted without deleting records."
                        }
                        db.execSQL(
                            "INSERT INTO document_chunks(id, documentId, content, json) VALUES (?, ?, ?, ?)",
                            arrayOf(chunk.id, chunk.documentId, chunk.content, json)
                        )
                        db.execSQL("INSERT INTO chunk_fts(chunkId, content) VALUES (?, ?)", arrayOf(chunk.id, chunk.content))
                    }
                }
                db.execSQL("DELETE FROM records WHERE kind = 'chunk'")
            }
        }

        fun open(context: Context, name: String = "coffee-creator.db"): CoffeeDatabase =
            Room.databaseBuilder(context.applicationContext, CoffeeDatabase::class.java, name)
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
