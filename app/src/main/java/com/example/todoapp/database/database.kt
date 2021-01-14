package com.example.todoapp.database

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [(ToDo::class)], version = 1)
@TypeConverters(converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): ToDoDao


    companion object {
        private var INSTANCE: AppDatabase? = null
        private val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getInstance(context: Context): AppDatabase {

            return INSTANCE ?:  synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    AppDatabase::class.java.simpleName
                ).build()
                INSTANCE = instance

                instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

class ToDoApplication: Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { ToDoRepository(database.todoDao()) }
}
