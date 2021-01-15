package com.example.todoapp.database

import kotlinx.coroutines.flow.Flow

class ToDoRepository(private val toDoDao: ToDoDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allToDo: Flow<List<ToDo>> = toDoDao.getAll()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    suspend fun insert(toDo: ToDo) {
        AppDatabase.databaseWriteExecutor.execute(fun () {
            toDoDao.insert(toDo)
        })
    }
}
