package com.o7solutions.meetnote

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.o7solutions.meetnote.databinding.FragmentFirstBinding
import java.io.IOException

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */


private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null


    //    For audio
    private var fileName: String = ""

//    private var recordButton: RecordButton? = null
    private var recorder: MediaRecorder? = null

//    private var playButton: PlayButton? = null
    private var player: MediaPlayer? = null
    private var playFlag = false

    private var runningFlag = false


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

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
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

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fileName = "${requireContext().externalCacheDir?.absolutePath}/audiorecordtest.3gp"

        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )

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
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
}