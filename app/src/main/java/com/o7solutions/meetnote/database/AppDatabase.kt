package com.o7solutions.meetnote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.o7solutions.meetnote.database.data_classes.AudioRecording
import com.o7solutions.meetnote.database.data_classes.TimedNote
import com.o7solutions.meetnote.database.interfaces.AudioRecordingDao
import com.o7solutions.meetnote.database.interfaces.TimedNoteDao

@Database(entities = [AudioRecording::class, TimedNote::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun audioRecordingDao(): AudioRecordingDao
    abstract fun timedNoteDao(): TimedNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "audio_notes_db" // Name of your database file
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // In production, you'd typically implement proper migrations.
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}