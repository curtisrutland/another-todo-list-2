package com.curtisrutland.atdl2.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.curtisrutland.atdl2.R
import com.curtisrutland.atdl2.adapter.TodoListCollectionAdapter
import com.curtisrutland.atdl2.constant.Extras
import com.curtisrutland.atdl2.data.TodoList
import com.curtisrutland.atdl2.extension.confirm
import com.curtisrutland.atdl2.extension.getDb
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.coroutines.experimental.bg
import kotlinx.coroutines.experimental.android.UI

class MainActivity : AppCompatActivity(), AnkoLogger {

    private val viewAdapter = TodoListCollectionAdapter()
    private val viewManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        subscribeToTodoLists()
    }

    private fun subscribeToTodoLists() {
        bg {
            getDb().getAllTodoLists()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { viewAdapter.updateData(it) }
        }
    }

    private fun setupRecyclerView() {
        viewAdapter.apply {
            onListItemDelete = { onTodoListDelete(it) }
            onListItemEdit = { onTodoListEdit(it) }

        }
        todoRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            itemAnimator = SlideInLeftAnimator()
            itemAnimator.changeDuration = 250
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun fabClicked(view: View) {
        val newTodoName = getString(R.string.default_todo_list_title)
        launch(UI) {
            val newTodoId = createNewTodoList(newTodoName).await()
            startTodoListActivity(newTodoId)
        }
    }

    private fun onTodoListDelete(todoList: TodoList) {
        val text = getString(R.string.confirm_delete)
        val title = getString(R.string.confirm_delete_title)
        confirm(title, text) { deleteTodoList(todoList) }
    }

    private fun onTodoListEdit(id: Long?) {
        startTodoListActivity(id)
    }

    private fun createNewTodoList(name: String): Deferred<Long> = bg {
        getDb().insertTodoList(TodoList(name))
    }

    private fun startTodoListActivity(id: Long?) {
        val intent = Intent(this, TodoListActivity::class.java)
        if (id != null) {
            intent.putExtra(Extras.TODO_ID.name, id)
        }
        startActivity(intent)
    }

    private fun deleteTodoList(todoList: TodoList): Deferred<Unit> = bg {
        getDb().deleteTodoList(todoList)
    }
}
