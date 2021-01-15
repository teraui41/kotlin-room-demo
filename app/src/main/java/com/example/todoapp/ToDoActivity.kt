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
import java.util.*

class ToDoActivity : AppCompatActivity() {
    var rv_adapter: RecyclerViewAdapter = RecyclerViewAdapter()

    private val todoViewModel: ToDoViewModel by viewModels {
        ToDoViewModelFactory((application as ToDoApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        // Register Dialog.
        val EditDialog = initDialog()

        // Init RecyclerView and bind to adapter.
        var rv: RecyclerView = findViewById(R.id.todo_list)
        rv.adapter = rv_adapter
        rv.layoutManager = LinearLayoutManager(this)

        todoViewModel.allWords.observe(owner = this) { todos ->
            // Update the cached copy of the words in the adapter.
            todos.let { rv_adapter.setItems(it) }
        }

        // init floatButtonListener
        val addButton: FloatingActionButton = findViewById(R.id.floatingActionButton)
        addButton.setOnClickListener { EditDialog.show() }
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

                todoViewModel.insert(
                    ToDo(
                        null,
                        title.toString(),
                        content.toString(),
                        Date().time,
                        Date().time
                    )
                )
            }))
            .setNegativeButton("CANCEL", null)

        return builder.create()
    }
}