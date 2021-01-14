package com.example.todoapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    @Query("SELECT * FROM ToDo")
    fun getAll(): Flow<List<ToDo>>

    @Query("SELECT * FROM ToDo WHERE uid = (:uid)")
    fun getBayId(uid: Int): ToDo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(todo: ToDo)

    @Delete
    fun delete(todo: ToDo)

    @Update
    fun update(todo: ToDo)
}
