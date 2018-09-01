package com.curtisrutland.atdl2.extension

import android.app.Activity
import android.content.Context
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.curtisrutland.atdl2.data.TodoDatabase
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*

fun Activity.getDb() = TodoDatabase.getInstance(this).todoDataDao()

fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.confirm(title: String, text: String, onResult: () -> Unit) {
    alert(text, title) {
        yesButton { onResult() }
        noButton { }
    }.show()
}

fun Context.yesNo(title: String, text: String, onResult: (Boolean) -> Unit) {
    alert(text, title) {
        yesButton { onResult(true) }
        noButton { onResult(false) }
    }.show()
}

fun Context.prompt(title: String?, hint: String?, onResult: (String) -> Unit) = prompt(title, hint, null, onResult)

fun Context.prompt(title: String?, hint: String?, initialText: String?, onResult: (String) -> Unit) {
    var et: EditText? = null
    alert {
        customView {
            verticalLayout {
                textView {
                    this.text = title
                    textSize = 28f
                }.lparams(matchParent, wrapContent) {
                    margin = dip(16)
                }
                et = editText {
                    this.hint = hint
                    this.setText(initialText)
                    textSize = 16f
                    inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                }.lparams(matchParent, wrapContent) {
                    leftMargin = dip(16)
                    rightMargin = dip(16)
                }
            }
            yesButton {
                onResult(et?.text.toString())
            }
            noButton { }
        }
    }.show()
    et?.let {
        launch(kotlinx.coroutines.experimental.android.UI) {
            delay(200)
            it.requestFocus()
            it.selectAll()
            showKeyboard(it)
        }

    }
}