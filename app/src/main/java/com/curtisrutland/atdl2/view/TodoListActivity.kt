package com.curtisrutland.atdl2.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.curtisrutland.atdl2.R
import com.curtisrutland.atdl2.adapter.TodoCollectionAdapter
import com.curtisrutland.atdl2.constant.Extras
import com.curtisrutland.atdl2.data.Todo
import com.curtisrutland.atdl2.data.TodoList
import com.curtisrutland.atdl2.extension.*
import com.curtisrutland.atdl2.helpers.SwipeToDeleteCallback
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

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
        setupRxSubscriptions()
        setupHandlers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.todo_action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.editTitleAction -> {
            prompt("New Todo List Name", "New Name") {
                updateTodoListName(it)
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun addNewTodo(source: View) {
        val text = newTodoEditText.text.toString()
        newTodoEditText.text.clear()
        hideKeyboard()
        layoutRoot.requestFocus()
        insertTodo(text)
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
            itemAnimator.changeDuration = 250
        }
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                toast("Delete ${viewHolder?.adapterPosition}")
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(todoRecyclerView)
    }

    private fun subscribeToTodoList() {
        launch(UI) {
            todoList = bg {
                val db = getDb()
                db.getTodoListTodos(todoListId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { viewAdapter.updateData(it) }
                db.getTodoList(todoListId)
            }.await()
            todoList?.let { supportActionBar?.title = it.name }
        }
    }

    private fun setupRxSubscriptions() {
        RxTextView.afterTextChangeEvents(newTodoEditText)
                .subscribe { addTodoButton.isEnabled = it.view().text.isNotBlank() }
    }

    private fun setupHandlers() {
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
        val id = todo.id ?: throw Exception("Attempt to update a null todo id")
        val complete = !todo.complete
        launch(UI) {
            bg {
                getDb().setTodoComplete(id, complete)
            }.await()
            info { "Todo ${todo.text} complete to ${todo.complete}" }
        }

    }

    private fun updateTodoListName(name: String) {
        if (name.isEmpty()) return
        if (todoList?.name?.trim() == name.trim()) return
        todoList?.name = name
        todoList?.let {
            launch(UI) {
                bg {
                    getDb().updateTodoList(it)
                }.await()
                supportActionBar?.title = name
                snackbar(layoutRoot, "Updated Todo List Name!")
            }
        }

    }
}
