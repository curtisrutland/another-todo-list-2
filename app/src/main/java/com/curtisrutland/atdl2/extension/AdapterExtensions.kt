package com.curtisrutland.atdl2.extension

import android.support.v7.widget.RecyclerView

fun <T> RecyclerView.Adapter<*>.handleUpdateData(
        data: List<T>,
        oldData: List<T>,
        areItemsDifferent: (before: T, after: T) -> Boolean) {
    val max = Math.max(oldData.size, data.size) - 1
    for (i in 0..max) {
        when {
            i > data.size - 1 -> notifyItemRemoved(i)
            i > oldData.size - 1 -> notifyItemInserted(i)
            areItemsDifferent(oldData[i], data[i]) -> notifyItemChanged(i)
        }
    }
}