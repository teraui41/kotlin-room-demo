package com.example.todoapp.database

import androidx.room.*
import java.util.*

@Entity
data class ToDo(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "title") val title:String?,
    @ColumnInfo(name = "content") val content: String?
)
