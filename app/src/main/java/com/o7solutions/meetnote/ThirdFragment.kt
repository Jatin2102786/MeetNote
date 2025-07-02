package com.o7solutions.meetnote

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.o7solutions.meetnote.adapters.MeetingsAdapter
import com.o7solutions.meetnote.constants.Constant
import com.o7solutions.meetnote.data_classes.Meeting
import com.o7solutions.meetnote.databinding.FragmentAddMeetingBinding
import com.o7solutions.meetnote.databinding.FragmentThirdBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdFragment : Fragment(), MeetingsAdapter.OnItemClick {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentThirdBinding
    private lateinit var adapter: MeetingsAdapter
    private var meetList: ArrayList<Meeting> = ArrayList()
    private lateinit var db: FirebaseFirestore
    var searchFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

//        if (binding != null){
        binding.apply {
            addMeet.setOnClickListener {
                findNavController().navigate(R.id.addMeetingFragment)
            }

            adapter = MeetingsAdapter(meetList, this@ThirdFragment)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
        }
//        }

        binding.selectDate.setOnClickListener {
            showDatePicker()
        }
        getMeetings()


        binding.refreshButton.setOnClickListener {
            searchFlag = false
            binding.selectDate.text = "Select Date"
            getMeetings()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.d("Meetings list", meetList.toString())
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")

        builder.setTheme(R.style.ThemeOverlay_App_DatePicker)

        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener { selection ->

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = format.format(calendar.time)
            binding.selectDate.text = formattedDate
            searchFlag = true
            getMeetings()
        }


        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    fun getMeetings() {
        meetList.clear()
        db.collection(Constant.meetingCol)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore", "Error: ${error.message}")
                    return@addSnapshotListener
                }

                if (value != null) {


                    for (doc in value) {
                        val meetingItem = doc.toObject(Meeting::class.java)

                        if (searchFlag) {
                            if (meetingItem.shownDate == binding.selectDate.text.toString()) {
                                meetList.add(meetingItem)
                            }
                        } else {
                            meetList.add(meetingItem)
                        }

                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }


    override fun onClick(position: Int) {

        val bundle = Bundle()
        bundle.putString("meetingId", meetList[position].meetingId.toString())
        bundle.putString("meetingName", meetList[position].meetingName)
        bundle.putString("meetingDesc", meetList[position].meetingDescription)
        bundle.putString("showTime", meetList[position].shownTime)
        bundle.putString("showDate", meetList[position].shownDate)
        findNavController().navigate(R.id.viewMeetingFragment, bundle)

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThirdFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}