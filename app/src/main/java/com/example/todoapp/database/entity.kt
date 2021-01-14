package com.example.todoapp.database

import androidx.annotation.NonNull
import androidx.room.*
import java.util.*

@Entity
data class ToDo(
    @NonNull
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "title") val title:String?,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "create_date" ) val createDate: Long?,
    @ColumnInfo(name = "update_date") val updateDate: Long?
)
