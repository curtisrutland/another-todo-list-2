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
import com.curtisrutland.atdl2.data.TodoList
import com.curtisrutland.atdl2.extension.*
import com.curtisrutland.atdl2.helpers.SwipeToDeleteCallback
import com.jakewharton.rxbinding2.widget.RxTextView
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.design.snackbar
import org.ocpsoft.prettytime.PrettyTime

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

        getTodoList()
        setupRxSubscriptions()
        setupHandlers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.todo_action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.editTitleAction -> updateTodoListName()
        R.id.deleteListAction -> deleteTodoList()
        else -> super.onOptionsItemSelected(item)
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
        todoList?.id?.let { id ->
            launch(UI) {
                val db = bg { getDb() }.await()
                viewAdapter.setTodoListId(id, db)
            }
        }
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
                viewHolder?.let {
                    viewAdapter.deleteItemAt(it.adapterPosition)
                }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(todoRecyclerView)
    }

    private fun getTodoList() {
        launch(UI) {
            todoList = bg { getDb().getTodoList(todoListId) }.await()
            todoList?.let {
                supportActionBar?.title = it.name
                setupRecyclerView()
            }

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
        viewAdapter.onItemLongClick = {
            val message = "Created ${PrettyTime().format(it.createdOn)}"
            snackbar(layoutRootCoordinator, message)
        }
    }

    private fun insertTodo(text: String) {
        if (text.isNotBlank()) {
            viewAdapter.addItem(text)
        }
    }


    private fun updateTodoListName(): Boolean {
        prompt("New Todo List Name", "New Name", todoList?.name) { name ->
            if (name.isEmpty() || todoList?.name?.trim() == name.trim())
                return@prompt
            todoList?.name = name
            todoList?.let {
                launch(UI) {
                    bg { getDb().updateTodoList(it) }.await()
                    supportActionBar?.title = name
                    snackbar(layoutRootCoordinator, "Updated Todo List Name!")
                }
            }
        }
        return true
    }

    private fun deleteTodoList(): Boolean {
        confirm("Delete Todo List?", "This will remove all todos as well. Press OK to delete.") {
            todoList?.let { todoList ->
                launch(UI) {
                    bg {
                        getDb().deleteTodoList(todoList)
                    }.await()
                    finish()
                }
            }
        }
        return true
    }
}
