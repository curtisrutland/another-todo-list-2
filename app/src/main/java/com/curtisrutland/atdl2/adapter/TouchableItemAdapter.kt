package com.curtisrutland.atdl2.adapter


interface TouchableItemAdapter<T> {
    var onItemClick: ((T) -> Unit)?
    fun itemClicked(it: T) = onItemClick?.invoke(it)
}