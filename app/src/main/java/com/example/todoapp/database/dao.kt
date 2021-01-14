package com.example.todoapp.database

import androidx.room.*

@Dao
interface ToDoDao {
    @Query("SELECT * FROM ToDo")
    fun getAll():List<ToDo>

    @Query("SELECT * FROM ToDo WHERE uid = (:uid)")
    fun getBayId(uid: Int): ToDo

    @Insert
    fun insert(todo: ToDo)

    @Delete
    fun delete(todo: ToDo)

    @Update
    fun update(todo: ToDo)
}
