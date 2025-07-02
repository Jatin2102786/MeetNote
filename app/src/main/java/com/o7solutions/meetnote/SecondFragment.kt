package com.o7solutions.meetnote

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.o7solutions.meetnote.adapters.RecordAdapter
import com.o7solutions.meetnote.database.AppDatabase
import com.o7solutions.meetnote.database.data_classes.AudioRecording
import com.o7solutions.meetnote.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), RecordAdapter.OnClick {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: RecordAdapter
    private lateinit var db: AppDatabase
    private var recordList: ArrayList<AudioRecording> = ArrayList()
    var LOG_TAG = "Second Fragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())





        binding.apply {

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = RecordAdapter(this@SecondFragment,recordList)
            recyclerView.adapter = adapter

        }

        lifecycleScope.launch {
            recordList.addAll(db.audioRecordingDao().getAllRecordings())
            adapter.notifyDataSetChanged()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun visit(position: Int) {

        var bundle = Bundle()
        bundle.putInt("id",recordList[position].id.toInt())
        findNavController().navigate(R.id.viewMeetFragment,bundle)
    }


}