package com.o7solutions.meetnote.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.o7solutions.meetnote.R
import com.o7solutions.meetnote.constants.Functions
import com.o7solutions.meetnote.database.AppDatabase
import com.o7solutions.meetnote.database.data_classes.AudioRecording
import com.o7solutions.meetnote.database.data_classes.TimedNote
import com.o7solutions.meetnote.databinding.FragmentViewMeetBinding
import kotlinx.coroutines.launch
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ViewMeetFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var recordingId: Int = 0
    private lateinit var db: AppDatabase
    private lateinit var binding: FragmentViewMeetBinding
    private var player: MediaPlayer? = null
    private lateinit var audioRecording: AudioRecording
   var isPlaying = false
    private var startedOnce = false
    private var notesList: List<TimedNote> = mutableListOf()
    private var handler = Handler()
    private var updateSeekBarRunnable: Runnable = Runnable {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            recordingId = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewMeetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionIV.setBackgroundResource(R.drawable.play)
        db = AppDatabase.getInstance(requireActivity())

        lifecycleScope.launch {
            audioRecording = db.audioRecordingDao().getRecordingById(recordingId.toLong())!!
            notesList = db.timedNoteDao().getNotesForRecording(recordingId)
            setupActionButton()
        }

        setupSeekBar()
    }

    private fun setupActionButton() {
        binding.actionBtn.setOnClickListener {
            if (isPlaying) {
                player?.pause()
                binding.actionIV.setBackgroundResource(R.drawable.play)
            } else {
                binding.actionIV.setBackgroundResource(R.drawable.pause)
                if (startedOnce && player != null) {
                    player?.start()
                } else {
                    startPlaying(audioRecording.recordingPath)
                    startedOnce = true
                }
            }
            isPlaying = !isPlaying
        }
    }

    private fun setupSeekBar() {
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && player != null) {
                    player!!.seekTo(progress)
                    val currentPos = player!!.currentPosition
                    val totalDuration = player!!.duration
                    binding.timeTextView.text =
                        "${Functions.formatTime(currentPos)} / ${Functions.formatTime(totalDuration)}"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startPlaying(fileName: String) {
        if (player != null && !player!!.isPlaying) {
            player?.start()
            return
        }

        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()

                binding.seekbar.max = duration

                updateSeekBarRunnable = object : Runnable {
                    override fun run() {
                        if (player != null && player!!.isPlaying) {
                            val currentPos = player!!.currentPosition
                            val totalDuration = player!!.duration

                            val currentNote = notesList
                                .filter { it.timestampMs <= currentPos }
                                .maxByOrNull { it.timestampMs }

                            binding.noteET.text = currentNote?.noteText ?: ""
                            binding.seekbar.progress = currentPos
                            binding.timeTextView.text =
                                "${Functions.formatTime(currentPos)} / ${Functions.formatTime(totalDuration)}"

                            handler.postDelayed(this, 500)
                        }
                    }
                }
                handler.post(updateSeekBarRunnable)

                // Handle when playback finishes
                setOnCompletionListener {
                    this@ViewMeetFragment.isPlaying = false
                    startedOnce = false
                    binding.actionIV.setBackgroundResource(R.drawable.play)
                    binding.seekbar.progress = 0
                    handler.removeCallbacks(updateSeekBarRunnable)
                }

            } catch (e: IOException) {
                Log.e("ViewMeetFragment", "prepare() failed: ${e.message}")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        handler.removeCallbacks(updateSeekBarRunnable)
        binding.seekbar.progress = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlaying()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewMeetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
