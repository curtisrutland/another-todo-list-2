package com.curtisrutland.atdl2.data

import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

interface ITimestampedTable {
    var createdOn: Date?

    val createdOnString: String
        get() = PrettyTime().format(createdOn)
}