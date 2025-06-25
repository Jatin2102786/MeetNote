package com.o7solutions.meetnote.database.data_classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "timed_notes",
    foreignKeys = [ForeignKey(
        entity = AudioRecording::class,
        parentColumns = ["id"],
        childColumns = ["recordingId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TimedNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "recordingId")
    val recordingId: Long,
    val timestampMs: Long,
    val noteText: String
)