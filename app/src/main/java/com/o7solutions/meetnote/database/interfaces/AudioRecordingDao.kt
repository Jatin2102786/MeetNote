package com.o7solutions.meetnote.database.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.o7solutions.meetnote.database.data_classes.AudioRecording
import com.o7solutions.meetnote.database.data_classes.AudioRecordingWithNotes

@Dao
interface AudioRecordingDao {
    @Insert
    suspend fun insert(recording: AudioRecording): Long

    @Query("SELECT * FROM audio_recordings ORDER BY timestamp DESC")
    suspend fun getAllRecordings(): List<AudioRecording>

    @Query("SELECT * FROM audio_recordings WHERE id = :recordingId")
    suspend fun getRecordingById(recordingId: Long): AudioRecording?

    @Query("DELETE FROM audio_recordings WHERE id = :recordingId")
    suspend fun deleteRecording(recordingId: Long)

    @Transaction // Ensures the query is atomic
    @Query("SELECT * FROM audio_recordings WHERE id = :recordingId")
    suspend fun getRecordingWithNotes(recordingId: Long): AudioRecordingWithNotes?

    @Transaction
    @Query("SELECT * FROM audio_recordings ORDER BY timestamp DESC")
    suspend fun getAllRecordingsWithNotes(): List<AudioRecordingWithNotes>
}