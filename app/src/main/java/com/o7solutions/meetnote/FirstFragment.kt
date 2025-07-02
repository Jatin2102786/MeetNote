package com.o7solutions.meetnote

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.o7solutions.meetnote.database.AppDatabase
import com.o7solutions.meetnote.database.data_classes.AudioRecording
import com.o7solutions.meetnote.database.data_classes.TimedNote
import com.o7solutions.meetnote.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch
import java.io.IOException

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var playFlag = false
    private var runningFlag = false

    private var saveFlag = false
    private var recordStartTime: Long = 0
    private var userEnteredName = " "
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    private lateinit var db: AppDatabase
    private val notesList = ArrayList<TimedNote>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )



        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())


        binding.apply {
            actionButton.setOnClickListener {
                playFlag = !playFlag
                onRecord(playFlag)
                if (!playFlag) {
                    lottieAnimationView.visibility = View.GONE
                    actionButton.text = "Start"

                    saveButton.visibility = View.VISIBLE
                } else {
                    lottieAnimationView.visibility = View.VISIBLE
                    actionButton.text = "Stop"


                }
            }

            saveButton.setOnClickListener {
                showFileNameInputDialog()
            }

            addNoteBTN.setOnClickListener {
                val noteText = noteET.text.toString()
                val noteTime = SystemClock.elapsedRealtime() - recordStartTime

                if (noteText.isBlank()) {
                    Toast.makeText(requireContext(), "Note cannot be empty!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newNote = TimedNote(
                    recordingId = 0,
                    noteText = noteText,
                    timestampMs = noteTime
                )
                notesList.add(newNote)
                noteET.text?.clear()
            }

            playBTN.setOnClickListener {
                runningFlag = !runningFlag
                onPlay(runningFlag)
                if (!runningFlag) {
                    lottieAnimationView.visibility = View.GONE
                    playBTN.text = "Play"
                } else {
                    playBTN.text = "Pause"
                    lottieAnimationView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onRecord(start: Boolean) {
        if (start) startRecording() else stopRecording()
    }

    private fun onPlay(start: Boolean) {
        if (start) startPlaying() else stopPlaying()
    }

    private fun startRecording() {

        val tempFile = "${requireContext().externalCacheDir?.absolutePath}/temp_recording.3gp"
        fileName = tempFile

        recordStartTime = SystemClock.elapsedRealtime()
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun showFileNameInputDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter File Name")

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val paddingDp = 20
            val density = resources.displayMetrics.density
            val paddingPx = (paddingDp * density).toInt()
            setPadding(paddingPx, paddingPx / 2, paddingPx, paddingPx / 2)
        }

        val inputEditText = EditText(requireContext()).apply {
            hint = "e.g., myrecording"
            maxLines = 1
            setSingleLine(true)
        }

        container.addView(inputEditText)
        builder.setView(container)

        builder.setPositiveButton("OK") { dialog, _ ->
            val fileNameET = inputEditText.text.toString().trim()

            if (fileNameET.isNotEmpty()) {
                userEnteredName = fileNameET
                fileName = "${requireContext().externalCacheDir?.absolutePath}/$userEnteredName.3gp"

                val audioInstance = AudioRecording(
                    fileName = userEnteredName,
                    recordingPath = fileName,
                    timestamp = System.currentTimeMillis(),
                    audioRecordingTime = SystemClock.elapsedRealtime() - recordStartTime
                )

                lifecycleScope.launch {
                    db.audioRecordingDao().insert(audioInstance)
                    val lastId = db.audioRecordingDao().getLastItemId()

                    if (lastId != null) {
                        notesList.forEach {
                            it.recordingId = lastId
                            db.timedNoteDao().insert(it)
                        }
                        Toast.makeText(requireContext(), "Audio and notes saved!", Toast.LENGTH_SHORT).show()
                        notesList.clear()
                    } else {
                        Toast.makeText(requireContext(), "Failed to save notes. Recording ID missing.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "File name cannot be empty!", Toast.LENGTH_SHORT).show()
            }


            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            Toast.makeText(requireContext(), "File name input cancelled.", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }

        binding.saveButton.visibility = View.GONE
        builder.create().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }

        if (!permissionToRecordAccepted) {
            Toast.makeText(requireContext(), "Unable to record audio", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
