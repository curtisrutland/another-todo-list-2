package com.curtisrutland.atdl2.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.Date

@Entity
data class TodoList(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        var name: String,
        override var createdOn: Date?
): ITimestampedTable {
    constructor(): this(null, "", null)
    constructor(name: String): this(null, name, Date())
}