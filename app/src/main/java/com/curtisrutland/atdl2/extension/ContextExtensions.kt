package com.curtisrutland.atdl2.extension

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.curtisrutland.atdl2.data.TodoDatabase
import kotlinx.android.synthetic.main.input_prompt.view.*
import org.jetbrains.anko.*

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
        noButton { }
    }.show()
}

fun Context.yesNo(title: String, text: String, onResult: (Boolean) -> Unit) {
    alert(text, title) {
        yesButton { onResult(true) }
        noButton { onResult(true) }
    }.show()
}

fun Context.prompt(title: String?, hint: String?, onResult: (String) -> Unit) {
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
                    textSize = 16f
                    requestFocus()
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
}