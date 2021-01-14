package com.example.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todoapp.database.AppDatabase
import com.example.todoapp.database.ToDo

class ToDoActivity : AppCompatActivity() {
    var todos: List<ToDo> = listOf()
    var adapterff: RecyclerViewAdapter = RecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_to_do)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "testDB"
        ).build()

        var rv: RecyclerView = findViewById(R.id.todo_list)
        rv.adapter = adapterff
        rv.layoutManager = LinearLayoutManager(this)

        Thread(fun() {
            todos = db.todoDao().getAll()
            runOnUiThread(fun() {
                adapterff.setItems(todos)
            })
        }).start()
    }

    class RecyclerViewAdapter() :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        var data: List<ToDo> = listOf()

        fun setItems(dataSet: List<ToDo>) {
            data = dataSet
            this.notifyDataSetChanged()
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView

            init {
                // Define click listener for the ViewHolder's View.
                textView = view.findViewById(R.id.textView)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.textView.text = data[position].title
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = data.size
    }
}