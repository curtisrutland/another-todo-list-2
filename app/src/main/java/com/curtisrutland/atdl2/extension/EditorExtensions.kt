package com.curtisrutland.atdl2.extension

import android.view.inputmethod.EditorInfo
import android.widget.TextView

fun TextView.onEnter(action: () -> Unit) {
    this.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            action()
            true
        } else {
            false
        }
    }
}