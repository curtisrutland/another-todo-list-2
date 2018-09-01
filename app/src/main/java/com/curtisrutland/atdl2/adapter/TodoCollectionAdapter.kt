package com.curtisrutland.atdl2.adapter

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.curtisrutland.atdl2.R
import com.curtisrutland.atdl2.data.Todo
import com.curtisrutland.atdl2.data.TodoDataDao
import kotlinx.android.synthetic.main.todo_collection_item.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg

class TodoCollectionAdapter : RecyclerView.Adapter<TodoCollectionAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var data = listOf<Todo>()
    private var db: TodoDataDao? = null
    private var todoListId: Long? = null

    var checkIcon: Drawable? = null
    var checkBoxIcon: Drawable? = null

    fun deleteItemAt(position: Int) {
        val todo = data[position]
        launch(UI) {
            bg { db?.deleteTodos(todo) }.await()
            data -= todo
            notifyItemRemoved(position)
        }
    }

    fun addItem(text: String) {
        val todo = Todo(todoListId, text)
        launch(UI) {
            val newTodo = bg {
                val id = db?.insertTodo(todo)
                if (id != null)
                    db?.getTodo(id)
                else
                    null
            }.await()
            if (newTodo != null) {
                data += newTodo
                notifyItemInserted(data.size - 1)
            }
        }
    }

    fun setTodoListId(todoListId: Long, db: TodoDataDao) {
        this.db = db
        this.todoListId = todoListId
        launch(UI) {
            data = bg { db.getTodoListTodos(todoListId) }.await()
            data.forEachIndexed { i, _ -> notifyItemInserted(i) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_collection_item, parent, false)

        return TodoCollectionAdapter.ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = data[position]
        holder.view.apply {
            setOnClickListener { toggleItem(todo, position) }
            todoTextView.apply {
                text = todo.text
                paintFlags = if (todo.complete) {
                    setTypeface(typeface, Typeface.ITALIC)
                    paintFlags or Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                } else {
                    setTypeface(null, Typeface.NORMAL)
                    paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
            checkImageView.setImageDrawable(
                    if (todo.complete) {
                        checkIcon
                    } else {
                        checkBoxIcon
                    }
            )
        }
    }

    private fun toggleItem(todo: Todo, position: Int) {
        todo.toggle()
        launch(UI) {
            bg {
                db?.updateTodo(todo)
            }.await()
            notifyItemChanged(position, todo)
        }
    }
}