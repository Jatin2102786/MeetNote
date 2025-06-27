package com.o7solutions.meetnote.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.o7solutions.meetnote.R
import com.o7solutions.meetnote.database.AppDatabase
import com.o7solutions.meetnote.database.data_classes.AudioRecording
import com.o7solutions.meetnote.databinding.FragmentViewMeetBinding
import kotlinx.coroutines.launch
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewMeetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewMeetFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var recordingId: Int = 0
    private lateinit var db : AppDatabase
    private lateinit var binding: FragmentViewMeetBinding
    private var player: MediaPlayer? = null
    lateinit var audioRecording: AudioRecording

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            recordingId = it?.getInt("id")!!

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      binding = FragmentViewMeetBinding.inflate(layoutInflater)
      return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireActivity())
        lifecycleScope.launch {
            audioRecording = db.audioRecordingDao().getRecordingById(recordingId.toLong())!!
        }
        binding.apply {
            playBTN.setOnClickListener {
                startPlaying(audioRecording.recordingPath)
            }

            stopBTN.setOnClickListener {
                stopPlaying()
            }
        }
    }

    private fun startPlaying(fileName: String) {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                isLooping = true
                start()
            } catch (e: IOException) {
                Log.e("View Fragment", "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewMeetFragment.
         */
        // TODO: Rename and change types and number of parameters
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