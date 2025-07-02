package com.o7solutions.meetnote.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.FirebaseFirestore
import com.o7solutions.meetnote.R
import com.o7solutions.meetnote.constants.Constant
import com.o7solutions.meetnote.data_classes.Meeting
import com.o7solutions.meetnote.databinding.FragmentAddMeetingBinding
import java.text.DecimalFormat
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
 * Use the [AddMeetingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddMeetingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentAddMeetingBinding

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
        binding = FragmentAddMeetingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        binding.selectDateButton.setOnClickListener {
            showDatePicker()
        }

        binding.selectTimeButton.setOnClickListener {
            showTimePicker()
        }

        binding.addMeetBtn.setOnClickListener {
            addMeeting()
        }
    }

    private fun addMeeting() {
        binding.apply {



                if (meetName.text!!.isEmpty()) {
                    Toast.makeText(requireContext(), "Please add meeting title", Toast.LENGTH_SHORT)
                        .show()
                } else if (meetDesc.text!!.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please add meeting description",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (selectDateButton.text.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please select meeting date",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (selectTimeButton.text.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please select meeting time",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    var meetingId = System.currentTimeMillis()
                    var newMeet = Meeting(meetingId,
                        meetName.text.toString(),
                        meetDesc.text.toString(),
                        selectDateButton.text.toString(),
                        selectTimeButton.text.toString())
                    db
                        .collection(Constant.meetingCol)
                        .document(meetingId.toString())
                        .set(newMeet)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Meeting added successfully!", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "error occurred while adding meeting!", Toast.LENGTH_SHORT).show()

                        }

                }

        }
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
            binding.selectDateButton.text = formattedDate
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Time")

            .setTheme(R.style.ThemeOverlay_App_TimePicker)
            .build()

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute

            val formattedTime = formatTime(hour, minute)
            binding.selectTimeButton.text = formattedTime
        }

        picker.show(childFragmentManager, "TIME_PICKER")
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val df = DecimalFormat("00")
        return when {
            hour == 0 -> "12:${df.format(minute)} AM"
            hour == 12 -> "12:${df.format(minute)} PM"
            hour < 12 -> "$hour:${df.format(minute)} AM"
            else -> "${hour - 12}:${df.format(minute)} PM"
        }
    }


//    fun showRecyclerDialog(context: Context, itemList: List<String>) {
//        val dialogView = LayoutInflater.from(context).inflate(R.layout.person_recycler_layout, null)
//        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.person_recycler_view)
//
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = MyAdapter(itemList)
//
//        AlertDialog.Builder(context)
//            .setTitle("Select an Item")
//            .setView(dialogView)
//            .setNegativeButton("Cancel", null)
//            .show()
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddMeetingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddMeetingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}