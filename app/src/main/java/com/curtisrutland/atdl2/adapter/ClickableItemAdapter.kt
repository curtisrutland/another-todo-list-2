package com.curtisrutland.atdl2.adapter


interface ClickableItemAdapter<T> {
    var onItemClick: ((T) -> Unit)?
    fun itemClicked(it: T) = onItemClick?.invoke(it)
}