package com.o7solutions.meetnote.data_classes

import android.provider.ContactsContract

data class Users(
    val name: String,
    val email: String,
    val designation: String
) {
    constructor(): this("","","")
}
