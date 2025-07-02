package com.o7solutions.meetnote.data_classes

import com.google.firebase.Timestamp

data class Meeting(
    var meetingId: Long,
    var meetingName: String,
    var meetingDescription: String,
    var shownDate: String,
    var shownTime: String,
//    var userList: ArrayList<Users>
) {
    constructor(): this(0,"","","","")
}
