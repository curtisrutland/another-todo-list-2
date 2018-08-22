package com.curtisrutland.atdl2.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.curtisrutland.atdl2.R
import com.curtisrutland.atdl2.data.Todo
import kotlinx.android.synthetic.main.todo_collection_item.view.*

class TodoCollectionAdapter
    : RecyclerView.Adapter<TodoCollectionAdapter.ViewHolder>() {

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    private var data = listOf<Todo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view = LayoutInflater.from(parent.context)
                 .inflate(R.layout.todo_collection_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = data[position]
        holder.view.todoTextView.text = todo.text
    }

    fun updateData(data: List<Todo>) {
        this.data = data
        notifyDataSetChanged()
    }
}