package com.curtisrutland.atdl2.extension

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.curtisrutland.atdl2.data.TodoDatabase
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

fun Activity.getDb() = TodoDatabase.getInstance(this).todoDataDao()

fun Fragment.hideKeyboard() {
    activity.hideKeyboard(view)
}

fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.confirm(title: String, text: String, onResult: () -> Unit) {
    alert(text, title) {
        yesButton { onResult() }
        noButton {  }
    }.show()
}

fun Context.yesNo(title: String, text: String, onResult: (Boolean) -> Unit) {
    alert(text, title) {
        yesButton { onResult(true) }
        noButton { onResult(true) }
    }
}