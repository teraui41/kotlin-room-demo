package com.example.todoapp.database

import androidx.room.TypeConverter
import java.util.*


// Android SQLite並不支援Date欄位，所以改成儲存Long並透過TypeConverter轉換
class converters {
    @TypeConverter
    fun getDate(timeStamp: Long): Date {
        return Date(timeStamp)
    }

    @TypeConverter
    fun setDate(dateTime: Date): Long {
        return dateTime.time
    }
}
