package com.curtisrutland.atdl2.extension

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.curtisrutland.atdl2.data.TodoDatabase

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