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
import kotlinx.android.synthetic.main.todo_collection_item.view.*

class TodoCollectionAdapter(
        var onItemDelete: ((item: Todo) -> Unit)? = null,
        var onItemTouch: ((item: Todo) -> Unit)? = null,
        var checkIcon: Drawable? = null,
        var checkBoxIcon: Drawable? = null
) : RecyclerView.Adapter<TodoCollectionAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var data = listOf<Todo>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_collection_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = data[position]
        val view = holder.view

        view.todoTextView.apply {
            text = todo.text
            paintFlags = if (todo.complete) {
                setTypeface(typeface, Typeface.ITALIC)
                paintFlags or Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
            } else {
                setTypeface(null, Typeface.NORMAL)
                paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            setOnClickListener {
                onItemTouch?.invoke(todo)
            }
        }

        view.deleteImageView.setOnClickListener { onItemDelete?.invoke(todo) }

        view.checkImageView.apply {
            setImageDrawable(
                    if (todo.complete) {
                        checkIcon
                    } else {
                        checkBoxIcon
                    }
            )
            setOnClickListener { onItemTouch?.invoke(todo) }
        }
    }

    fun updateData(data: List<Todo>) {
        this.data = data
        notifyDataSetChanged()
    }
}