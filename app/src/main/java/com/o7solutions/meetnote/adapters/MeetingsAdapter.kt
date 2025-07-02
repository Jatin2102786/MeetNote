package com.o7solutions.meetnote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.o7solutions.meetnote.R
import com.o7solutions.meetnote.data_classes.Meeting

class MeetingsAdapter(var meetList: ArrayList<Meeting>,
    var itemClick: OnItemClick): RecyclerView.Adapter<MeetingsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.meeting_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.apply {
            meetName.text = meetList[position].meetingName
            meetDesc.text = meetList[position].meetingDescription
            time.text = meetList[position].shownTime
            date.text = meetList[position].shownDate


            view.setOnClickListener {
//                itemClick.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return meetList.size
    }


    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        var meetName : TextView = view.findViewById(R.id.meetName)
        var meetDesc : TextView = view.findViewById(R.id.meetDesc)
        var time: TextView = view.findViewById(R.id.tvShownTime)
        var date: TextView = view.findViewById(R.id.tvShownDate)

    }

    interface OnItemClick {
        fun onClick(position: Int)
    }
}