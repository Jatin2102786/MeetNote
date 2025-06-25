package com.o7solutions.meetnote.database.data_classes

import androidx.room.Embedded
import androidx.room.Relation

data class AudioRecordingWithNotes(
    @Embedded val recording: AudioRecording, // The parent entity
    @Relation(
        parentColumn = "id",            // The primary key of the parent (AudioRecording)
        entityColumn = "recordingId"    // The foreign key in the child (TimedNote) that links to the parent
    )
    val notes: List<TimedNote>          // The list of child entities
)