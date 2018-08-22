package com.curtisrutland.atdl2.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.curtisrutland.atdl2.data.converter.Converters

@Database(entities = [TodoList::class, Todo::class], version = 2)
@TypeConverters(Converters::class)
abstract class TodoDatabase: RoomDatabase() {
    abstract fun todoDataDao(): TodoDataDao

    companion object {
        private var instance: TodoDatabase? = null

        fun getInstance(context: Context): TodoDatabase {
           if (instance == null) {
               synchronized(TodoDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext, TodoDatabase::class.java, "todo.db")
                            .fallbackToDestructiveMigration()
                            .build()
               }
           }
            return instance!!
        }
    }
}