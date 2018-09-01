package com.curtisrutland.atdl2.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.curtisrutland.atdl2.R
import com.curtisrutland.atdl2.data.TodoList
import com.curtisrutland.atdl2.extension.handleUpdateData
import kotlinx.android.synthetic.main.todo_list_collection_item.view.*

class TodoListCollectionAdapter :
        RecyclerView.Adapter<TodoListCollectionAdapter.ViewHolder>(),
        TouchableItemAdapter<Long?> {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override var onItemClick: ((Long?) -> Unit)? = null

    private var data = listOf<TodoList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_list_collection_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoList = data[position]
        holder.view.apply {
            todoListNameTextView.text = todoList.name
            createdOnTextView.text = todoList.createdOnString
            setOnClickListener { itemClicked(todoList.id) }
        }
    }

    fun updateData(data: List<TodoList>) {
        val oldData = this.data
        this.data = data
        handleUpdateData(data, oldData) { before, after ->
            before.id != after.id || before.name != after.name
        }
    }

}