package com.o7solutions.meetnote.database.data_classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_recordings")
data class AudioRecording(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L, // Primary key for the recording
    val fileName: String,
    val recordingPath: String, // Path to the audio file
    val timestamp: Long ,
    val audioRecordingTime: Long// Timestamp when the recording was created
)