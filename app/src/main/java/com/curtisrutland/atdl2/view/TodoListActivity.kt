package com.curtisrutland.atdl2.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.curtisrutland.atdl2.R
import com.curtisrutland.atdl2.adapter.TodoCollectionAdapter
import com.curtisrutland.atdl2.constant.Extras
import com.curtisrutland.atdl2.data.Todo
import com.curtisrutland.atdl2.data.TodoList
import com.curtisrutland.atdl2.extension.confirm
import com.curtisrutland.atdl2.extension.getDb
import com.curtisrutland.atdl2.extension.hideKeyboard
import com.curtisrutland.atdl2.extension.onEnter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.toast

class TodoListActivity : AppCompatActivity(), AnkoLogger {

    private val viewAdapter = TodoCollectionAdapter()
    private val viewManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private var todoListId = Long.MIN_VALUE
    private var todoList: TodoList? = null
    private var editing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        todoListId = intent.getLongExtra(Extras.TODO_ID.name, Long.MIN_VALUE)
        if (todoListId == Long.MIN_VALUE) {
            throw Exception("No ID Provided")
        }

        setupRecyclerView()
        subscribeToTodoList()
        setupRxSubscriptions()
        setupHandlers()
    }

    @Suppress("UNUSED_PARAMETER")
    fun addNewTodo(source: View) {
        val text = newTodoEditText.text.toString()
        newTodoEditText.text.clear()
        hideKeyboard()
        layoutRoot.requestFocus()
        insertTodo(text)
    }

    override fun onBackPressed() {
        if (editing) {
            showEditTitle(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun showEditTitle(show: Boolean = true) {
        if (show) {
            if (titleViewSwitcher.currentView == todoListNameTextView) {
                todoListNameEditText.setText(todoListNameTextView.text)
                titleViewSwitcher.showNext()
                editing = true
            }
        } else {
            if (titleViewSwitcher.currentView == todoListNameEditText) {
                titleViewSwitcher.showPrevious()
                hideKeyboard()
                editing = false
            }
        }
    }

    private fun setupRecyclerView() {
        viewAdapter.apply {
            checkIcon = getDrawable(R.drawable.ic_check_box_white_24dp)
            checkBoxIcon = getDrawable(R.drawable.ic_check_box_outline_blank_white_24dp)
        }
        todoRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            itemAnimator = SlideInLeftAnimator()
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
            todoListNameTextView.text = todoList?.name
        }
    }

    private fun setupRxSubscriptions() {
        RxTextView.afterTextChangeEvents(newTodoEditText)
                .subscribe { addTodoButton.isEnabled = it.view().text.isNotBlank() }
    }

    private fun setupHandlers() {
        todoListNameTextView.setOnLongClickListener {
            showEditTitle()
            true
        }

        todoListNameEditText.onEnter {
            showEditTitle(false)
            updateTodoListName()
        }

        newTodoEditText.onEnter {
            addNewTodo(newTodoEditText)
        }

        viewAdapter.apply {
            onItemDelete = { deleteTodo(it) }
            onItemTouch = { toggleTodoComplete(it) }

        }
    }

    private fun insertTodo(text: String) {
        if (text.isEmpty()) {
            return
        }
        val todo = Todo(todoListId, text)
        bg { getDb().insertTodo(todo) }
    }

    private fun deleteTodo(todo: Todo) {
        confirm("Delete Todo?", "Press OK to delete.") {
            launch(UI) {
                bg {
                    getDb().deleteTodos(todo)
                }.await()
                snackbar(layoutRoot, "Todo Deleted!")
            }
        }
    }

    private fun toggleTodoComplete(todo: Todo) {
        todo.complete = !todo.complete
        launch(UI) {
            bg {
                getDb().updateTodo(todo)
            }.await()
            //snackbar(layoutRoot, "${todo.text} - Toggled!")
        }

    }

    private fun updateTodoListName() {
        val name = todoListNameEditText.text.toString()
        if (todoList?.name?.trim().isNullOrEmpty() || todoList?.name == name) {
            return
        }
        todoList?.name = name
        val tdl = todoList ?: throw Exception("Sanity check failed")
        launch(UI) {
            bg {
                getDb().updateTodoList(tdl)
            }.await()
            todoListNameTextView.text = name
            snackbar(layoutRoot, "Updated Todo List Name!")
        }
    }
}
