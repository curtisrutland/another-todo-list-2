package com.curtisrutland.atdl2.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.curtisrutland.atdl2.R
import com.curtisrutland.atdl2.adapter.TodoListCollectionAdapter
import com.curtisrutland.atdl2.constant.Extras
import com.curtisrutland.atdl2.data.TodoList
import com.curtisrutland.atdl2.extension.getDb
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg

class MainActivity : AppCompatActivity(), AnkoLogger {

    private val viewAdapter = TodoListCollectionAdapter()
    private val viewManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        subscribeToTodoLists()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.projectLinkAction -> openProjectLink()
        else -> super.onOptionsItemSelected(item)
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
            onItemClick = { showTodoList(it) }
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
        createTodoList()
    }

    private fun createTodoList() {
        val newTodoName = getString(R.string.default_todo_list_title)
        launch(UI) {
            val newTodoId = bg {
                getDb().insertTodoList(TodoList(newTodoName))
            }.await()
            startTodoListActivity(newTodoId)
        }
    }

    private fun showTodoList(id: Long?) {
        startTodoListActivity(id)
    }

    private fun startTodoListActivity(id: Long?) {
        val intent = Intent(this, TodoListActivity::class.java)
        if (id != null) {
            intent.putExtra(Extras.TODO_ID.name, id)
        }
        startActivity(intent)
    }

    private fun openProjectLink(): Boolean {
        val projectLink = getString(R.string.project_link)
        val uri = Uri.parse(projectLink)
        CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorBlack))
                .build()
                .launchUrl(this, uri)
        return true
    }
}
