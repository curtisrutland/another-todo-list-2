package com.curtisrutland.atdl2.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.curtisrutland.atdl2.R
import com.curtisrutland.atdl2.adapter.TodoCollectionAdapter
import com.curtisrutland.atdl2.constant.Extras
import com.curtisrutland.atdl2.data.Todo
import com.curtisrutland.atdl2.data.TodoList
import com.curtisrutland.atdl2.extension.getDb
import com.curtisrutland.atdl2.extension.hideKeyboard
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.concurrent.TimeUnit

class TodoListActivity : AppCompatActivity(), AnkoLogger {

    private val viewAdapter = TodoCollectionAdapter()
    private val viewManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private var todoListId = Long.MIN_VALUE
    private var todoList: TodoList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        todoListId = intent.getLongExtra(Extras.TODO_ID.name, Long.MIN_VALUE)
        if (todoListId == Long.MIN_VALUE) {
            throw Exception("No ID Provided")
        }

        setupRecyclerView()
        subscribeToTodoList()

        RxTextView.afterTextChangeEvents(newTodoEditText)
                .subscribe { addTodoButton.isEnabled = it.view().text.isNotBlank() }

        RxTextView.afterTextChangeEvents(todoListNameEditText)
                .skipInitialValue()
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter { it.view().text.isNotEmpty() }
                .subscribe{ updateTodoListName(it.view().text.toString().trim())}
    }

    fun addTodoPressed(view: View) {
        val text = newTodoEditText.text.toString()
        newTodoEditText.text.clear()
        hideKeyboard()
        layoutRoot.requestFocus()
        insertTodo(text)
    }

    private fun setupRecyclerView() {
        todoRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun subscribeToTodoList() {
        val todoListDeferred = bg {
            val db = getDb()
            db.getTodoListTodos(todoListId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { viewAdapter.updateData(it) }
            db.getTodoList(todoListId)
        }

        launch(UI) {
            todoList = todoListDeferred.await()
            todoListNameEditText.setText(todoList?.name)
        }
    }

    private fun insertTodo(text: String) {
        if (text.isEmpty()) {
            return
        }
        val todo = Todo(todoListId, text)
        bg { getDb().insertTodo(todo) }
    }

    private fun updateTodoListName(name: String) {
        if(todoList?.name == name) {
            return
        }
        todoList?.name = name
        bg {
            getDb().updateTodoList(todoList!!)
        }
    }
}
