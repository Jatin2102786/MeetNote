package com.o7solutions.meetnote


import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.text.format.Time
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.room.CoroutinesRoom
import com.o7solutions.meetnote.database.AppDatabase
import com.o7solutions.meetnote.database.data_classes.AudioRecording
import com.o7solutions.meetnote.database.data_classes.TimedNote
import com.o7solutions.meetnote.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */


private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

@Suppress("DEPRECATION")
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null


    //    For audio
    private var fileName: String = ""

//    private var recordButton: RecordButton? = null
    private var recorder: MediaRecorder? = null

//    private var playButton: PlayButton? = null
    private var player: MediaPlayer? = null
    private var playFlag = false

    private var recordStartTime:Long = 0

    private var notesList: ArrayList<TimedNote> = ArrayList()

    private var runningFlag = false
    private var userEnteredName = " "
    private lateinit var db: AppDatabase


    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

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
        if (!permissionToRecordAccepted) Toast.makeText(
            requireContext(),
            "Unable to record audio",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onRecord(start: Boolean)
    = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean)
    = if (start) {
        startPlaying()
    } else {
        stopPlaying()
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

    private fun startRecording() {

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


    private val binding get() = _binding!!

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

        fileName = "${requireContext().externalCacheDir?.absolutePath}/$userEnteredName.3gp"
        binding.apply {
            actionButton.setOnClickListener {
                playFlag = !playFlag


                onRecord(playFlag)

                if (!playFlag) {
                    binding.lottieAnimationView.visibility = View.GONE
                    binding.actionButton.text = "Start"
                } else {
                    binding.lottieAnimationView.visibility = View.VISIBLE
                    binding.actionButton.text = "Stop"
                }
            }

            saveButton.setOnClickListener {
                userEnteredName = showFileNameInputDialog()


            }


            binding.addNoteBTN.setOnClickListener {
                val noteText = binding.noteET.text.toString()
                val noteTime = SystemClock.elapsedRealtime() - recordStartTime

                if (noteText.isBlank()) {
                    Toast.makeText(requireContext(), "Note cannot be empty!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                lifecycleScope.launch {

                   
                        val newNote = TimedNote(
                            recordingId = 0,
                            noteText = noteText,
                            timestampMs = noteTime
                        )
                        notesList.add(newNote)
                        binding.noteET.text?.clear() // Clear the note input field

                }
            }

            playBTN.setOnClickListener {
                runningFlag = !runningFlag

                onPlay(runningFlag)
                if (!runningFlag) {
                    binding.lottieAnimationView.visibility = View.GONE
                    binding.playBTN.text = "Play"
                } else {
                    binding.playBTN.text = "Pause"
                    binding.lottieAnimationView.visibility = View.VISIBLE

                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showFileNameInputDialog(): String {

        var fileNameET= " "
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter File Name")


        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.VERTICAL

        val paddingDp = 20
        val density = resources.displayMetrics.density
        val paddingPx = (paddingDp * density).toInt()
        container.setPadding(paddingPx, paddingPx / 2, paddingPx, paddingPx / 2)

        val inputEditText = EditText(requireContext())
        inputEditText.hint = "e.g., mydocument.txt"
        inputEditText.maxLines = 1
        inputEditText.setSingleLine(true)


        container.addView(inputEditText)


        builder.setView(container)


        builder.setPositiveButton("OK") { dialog, which ->
            fileNameET = inputEditText.text.toString().trim()

            // Check if the file name is empty
            if (fileNameET.isNotEmpty()) {
               userEnteredName = fileNameET

//                added data to database
                val audioInstance = AudioRecording(fileName = userEnteredName, recordingPath = fileName, timestamp = System.currentTimeMillis(), audioRecordingTime = SystemClock.elapsedRealtime()- recordStartTime)
                lifecycleScope.launch {
                    db.audioRecordingDao().insert(audioInstance)

                    for(i in notesList) {
                        notesList[i].recordingId = db.audioRecordingDao().getLastItemId()!!
                    }
                }
            } else {
                Toast.makeText(requireContext(), "File name cannot be empty!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }


        builder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(requireContext(), "File name input cancelled.", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }


        val dialog = builder.create()
        dialog.show()
        return  fileNameET
    }
}


//    //    removable code
//    internal inner class RecordButton(ctx: Context) : androidx.appcompat.widget.AppCompatButton(ctx) {
//
//        var mStartRecording = true
//
//        var clicker: OnClickListener = OnClickListener {
//            onRecord(mStartRecording)
//            text = when (mStartRecording) {
//                true -> "Stop recording"
//                false -> "Start recording"
//            }
//            mStartRecording = !mStartRecording
//        }
//
//        init {
//            text = "Start recording"
//            setOnClickListener(clicker)
//        }
//    }
//
//
//    //    removable code
//    internal inner class PlayButton(ctx: Context) : androidx.appcompat.widget.AppCompatButton(ctx) {
//        var mStartPlaying = true
//        var clicker: OnClickListener = OnClickListener {
//            onPlay(mStartPlaying)
//            text = when (mStartPlaying) {
//                true -> "Stop playing"
//                false -> "Start playing"
//            }
//            mStartPlaying = !mStartPlaying
//        }
//
//        init {
//            text = "Start playing"
//            setOnClickListener(clicker)
//        }
//    }



//        recordButton = RecordButton(requireContext())
//        playButton = PlayButton(requireContext())
//        val ll = LinearLayout(requireContext()).apply {
//            addView(
//                recordButton,
//                LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    0f
//                )
//            )
//            addView(
//                playButton,
//                LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    0f
//                )
//            )
//        }

//        return ll