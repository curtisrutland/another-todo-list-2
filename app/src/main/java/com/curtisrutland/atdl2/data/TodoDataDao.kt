package com.curtisrutland.atdl2.data

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface TodoDataDao {

    @Query("SELECT * from TodoList")
    fun getAllTodoLists(): Flowable<List<TodoList>>

    @Query("SELECT * from TodoList where id = :id")
    fun getTodoList(id: Long): TodoList

    @Delete
    fun deleteTodoList(todoList: TodoList)

    @Query("SELECT * from Todo where id = :id")
    fun getTodo(id: Long): Todo

    @Query("SELECT * from Todo where todoListId = :todoListId")
    fun getTodoListTodos(todoListId: Long): Flowable<List<Todo>>

    @Update
    fun updateTodoList(todoList: TodoList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodoList(todoList: TodoList): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodo(todo: Todo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodos(vararg todo: Todo): List<Long>

    @Update
    fun updateTodo(todo: Todo)

    @Delete
    fun deleteTodos(vararg todo: Todo)

    @Query("DELETE from TodoList")
    fun deleteAllTodoLists()

    @Query("DELETE from Todo")
    fun deleteAllTodos()
}