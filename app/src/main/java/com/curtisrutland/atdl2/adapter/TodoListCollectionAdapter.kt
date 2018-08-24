package com.curtisrutland.atdl2.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.curtisrutland.atdl2.R
import com.curtisrutland.atdl2.data.TodoList
import kotlinx.android.synthetic.main.todo_list_collection_item.view.*

class TodoListCollectionAdapter(
        private val onListItemDelete: (TodoList) -> Unit,
        private val onListItemEdit: (Long?) -> Unit
) : RecyclerView.Adapter<TodoListCollectionAdapter.ViewHolder>() {
    class ViewHolder(val view: View, val animation: Animation) : RecyclerView.ViewHolder(view)

    private var data = listOf<TodoList>()

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_list_collection_item, parent, false)

        return ViewHolder(view, AnimationUtils.loadAnimation(parent.context, android.R.anim.slide_in_left))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoList = data[position]
        holder.view.apply {
            todoListNameTextView.text = todoList.name
            createdOnTextView.text = todoList.createdOnString
            deleteButton.setOnClickListener { onListItemDelete(todoList) }
            editButton.setOnClickListener { onListItemEdit(todoList.id) }
        }
        setAnimation(holder, position)
    }

    private fun setAnimation(holder: ViewHolder, position: Int) {
        if (position > lastPosition) {
            holder.view.startAnimation(holder.animation)
            lastPosition = position
        }
    }

    fun updateData(data: List<TodoList>) {
        this.data = data
        notifyDataSetChanged()
    }

}