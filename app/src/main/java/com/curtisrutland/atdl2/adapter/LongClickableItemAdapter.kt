package com.curtisrutland.atdl2.adapter

interface LongClickableItemAdapter<T> {
    var onItemLongClick: ((T) -> Unit)?
    fun itemLongClicked(it: T) = onItemLongClick?.invoke(it)
}