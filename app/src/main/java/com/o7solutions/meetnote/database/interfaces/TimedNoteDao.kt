package com.o7solutions.meetnote.database.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.o7solutions.meetnote.database.data_classes.TimedNote

@Dao
interface TimedNoteDao {
    @Insert
    suspend fun insert(note: TimedNote)

    @Query("SELECT * FROM timed_notes WHERE recordingId = :recordingId ORDER BY timestampMs ASC")
    suspend fun getNotesForRecording(recordingId: Long): List<TimedNote>

    @Query("DELETE FROM timed_notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)

    @Query("DELETE FROM timed_notes WHERE recordingId = :recordingId")
    suspend fun deleteNotesForRecording(recordingId: Long)
}