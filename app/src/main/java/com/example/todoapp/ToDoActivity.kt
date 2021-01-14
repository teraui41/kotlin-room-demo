package com.example.todoapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.database.AppDatabase
import com.example.todoapp.database.ToDo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.concurrent.thread

class ToDoActivity : AppCompatActivity() {
    var todos: List<ToDo> = listOf()
    var adapterff: RecyclerViewAdapter = RecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newFragment = initDialog()
        setContentView(R.layout.activity_to_do)


        var rv: RecyclerView = findViewById(R.id.todo_list)
        rv.adapter = adapterff
        rv.layoutManager = LinearLayoutManager(this)

        Thread(fun() {
            todos = AppDatabase.getInstance(this)!!.todoDao().getAll()
            runOnUiThread(fun() {
                adapterff.setItems(todos)
            })
        }).start()

        val addButton: FloatingActionButton = findViewById(R.id.floatingActionButton)
        addButton.setOnClickListener { newFragment.show() }
    }

    class RecyclerViewAdapter() :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        var data: List<ToDo> = listOf()

        fun setItems(dataSet: List<ToDo>) {
            data = dataSet
            this.notifyDataSetChanged()
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val TitleTextView: TextView
            val ContentTextView: TextView
            val DateTimeTextView: TextView

            init {
                // Define click listener for the ViewHolder's View.
                TitleTextView = view.findViewById(R.id.todo_title)
                ContentTextView = view.findViewById(R.id.todo_content)
                DateTimeTextView = view.findViewById(R.id.todo_time)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.TitleTextView.text = data[position].title
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = data.size
    }

    fun initDialog(): Dialog {
        val linf = LayoutInflater.from(this)
        val inflator: View = linf.inflate(R.layout.alert_editor, null)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add ToDo")
            .setView(inflator)
            .setPositiveButton("CONFIRM", DialogInterface.OnClickListener(fun(dialog, id) {
                var title: Editable? = inflator.findViewById<EditText>(R.id.input_title).text
                var content: Editable? = inflator.findViewById<EditText>(R.id.input_content).text
                Thread(fun () {
                    AppDatabase.getInstance(this)!!.todoDao().insert(
                        ToDo(
                            null,
                            title.toString(),
                            content.toString(),
                            Date().time,
                            Date().time
                        )
                    )
                }).start()

            }))
            .setNegativeButton("CANCEL", DialogInterface.OnClickListener(fun(dialog, id) {
                //
            }))

        return builder.create()
    }
}