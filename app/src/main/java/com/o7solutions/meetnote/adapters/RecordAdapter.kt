package com.o7solutions.meetnote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.o7solutions.meetnote.R
import com.o7solutions.meetnote.constants.Functions
import com.o7solutions.meetnote.database.data_classes.AudioRecording

class RecordAdapter(val onClick: OnClick,var list: ArrayList<AudioRecording>):
    RecyclerView.Adapter<RecordAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        var note = list[position]
        holder.apply {
            nameTV.text = note.fileName
            dateTime.text = Functions.formatEpochMillisToDateTimeString(note.timestamp)
            view.setOnClickListener {
                onClick.visit(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {


        var nameTV: TextView = view.findViewById(R.id.nameTV)
        var dateTime: TextView = view.findViewById(R.id.timeTV)

    }

    interface OnClick {
        fun visit(position: Int)
    }
}