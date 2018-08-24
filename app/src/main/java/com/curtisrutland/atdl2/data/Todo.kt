package com.curtisrutland.atdl2.data

import android.arch.persistence.room.*
import java.util.Date

@Entity(
        foreignKeys = [
            (ForeignKey(entity = TodoList::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("todoListId"),
                    onDelete = ForeignKey.CASCADE))
        ]
)
data class Todo(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        var todoListId: Long?,
        var text: String,
        var complete: Boolean,
        override var createdOn: Date?
) : ITimestampedTable {
    constructor() : this(null, null, "", false, null)
    constructor(text: String) : this(null, null, text, false, Date())
    constructor(todoListId: Long?, text: String): this(null, todoListId, text, false, Date())

    override fun toString() = "Id: $id, text: '$text', complete: $complete, belongsTo: $todoListId"
}