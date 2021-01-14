package com.example.todoapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.database.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class ToDoActivity : AppCompatActivity() {
    var todos: Flow<List<ToDo>> = flowOf()
    var adapterff: RecyclerViewAdapter = RecyclerViewAdapter()

    private val wordViewModel: ToDoViewModel by viewModels {
        ToDoViewModelFactory((application as ToDoApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        // Register Dialog.
        val newFragment = initDialog()

        // Fetch db instance to global variable.
//        db = AppDatabase.getInstance(this)

        // Init RecyclerView and bind to adapter.
        var rv: RecyclerView = findViewById(R.id.todo_list)
        rv.adapter = adapterff
        rv.layoutManager = LinearLayoutManager(this)

        wordViewModel.allWords.observe(owner = this) { todos ->
            // Update the cached copy of the words in the adapter.
            todos.let { adapterff.setItems(it) }
        }
        // Note that data operation should put into thread.
//        Thread(fun() {
//            todos = db!!.todoDao().getAll()
//            runOnUiThread(fun() {
////                adapterff.setItems(todos.)
//            })
//        }).start()

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

//                Thread(fun () {
                    wordViewModel.insert(
                        ToDo(
                            null,
                            title.toString(),
                            content.toString(),
                            Date().time,
                            Date().time
                        )
                    )
//                }).start()

            }))
            .setNegativeButton("CANCEL", DialogInterface.OnClickListener(fun(dialog, id) {
                //
            }))

        return builder.create()
    }
}