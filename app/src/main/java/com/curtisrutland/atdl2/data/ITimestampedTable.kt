package com.curtisrutland.atdl2.data

import java.text.SimpleDateFormat
import java.util.*

interface ITimestampedTable {
    var createdOn: Date?

    val createdOnString: String
        get() = SimpleDateFormat("EEE MMM d h:mm a", Locale.getDefault()).format(createdOn)
}