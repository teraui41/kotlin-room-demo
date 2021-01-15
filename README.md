Android Room
===

###### tags: `android`

## Create Android App

Create a ToDo app for demo.
- Will add relation between table in the future.
- Try to connect room with react natvie.


## Add room and livedata

**build.gradle**

```json
dependencies {
    ...
    def room_version = "2.2.6"
    def lifecycle_version = "2.2.0"
    
    implementation "androidx.room:room-runtime:$room_version"
    kapt "androidx.room:room-compiler:$room_version"

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation "androidx.room:room-ktx:$room_version"

    // optional - Test helpers
    testImplementation "androidx.room:room-testing:$room_version"

    // Add lifecycle
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    ...
}
```

## Room Database

includes three parts

- Entity: Define **schema**.
- DAO(Data Access Object): Define query **SQL**.
- Database: Create database **instance**

Create an directory named **database**

Create directory`Entities` `Daos` `ViewModels` under database.

Note that all the operation should not doing at main thread.

### Entity

**ToDoEntity**



```kotlin
import androidx.annotation.Nullable
import androidx.room.*

@Entity
data class ToDo(
    @Nullable
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "title") val title:String?,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "create_date" ) val createDate: Long?,
    @ColumnInfo(name = "update_date") val updateDate: Long?
)
```

Note that SQLit don't support **date** type. Datetime must save as **long** before convert to datetime object.

### TypeConverter

```kotlin

import androidx.room.TypeConverter
import java.util.*

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
```

Then apply converter to database. Will show these part later.

## DAO - Data Access Object

```kotlin
import androidx.room.*

@Dao
interface ToDoDao {
    @Query("SELECT * FROM ToDo")
    fun getAll(): List<ToDo>

    @Query("SELECT * FROM ToDo WHERE uid = (:uid)")
    fun getBayId(uid: Int): ToDo

    @Insert
    fun insertAll(vararg todos: ToDo) // Add mutiple data.

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(todo: ToDo)

    @Delete
    fun delete(todo: ToDo)

    @Update
    fun update(todo: ToDo)
}
```

## Database

```kotlin

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

    // Like "static" in java. It is important concept of kotlin.
    companion object {
        // Declare database instancce.
        private var INSTANCE: AppDatabase? = null
        private val NUMBER_OF_THREADS = 4
        // Create Executor to avoid using Thread api.
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
```

## Inite Database in activity

**ToDo Activity**

```kotlin
    var todos: List<ToDo> = listOf()
    var rv_adapter: RecyclerViewAdapter = RecyclerViewAdapter()
    var db: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        // Register Dialog.
        val newFragment = initDialog()

        // Fetch db instance to global variable.
        db = AppDatabase.getInstance(this)

        // Init RecyclerView and bind to adapter.
        var rv: RecyclerView = findViewById(R.id.todo_list)
        rv.adapter = rv_adapter
        rv.layoutManager = LinearLayoutManager(this)

        // Note that data operation should put into thread.
        // setItems without liveData.
        Thread(fun() {
            todos = db!!.todoDao().getAll()
            runOnUiThread(fun() {
                 adapterff.setItems(todos.)
            })
        }).start()

        val addButton: FloatingActionButton = findViewById(R.id.floatingActionButton)
        addButton.setOnClickListener { newFragment.show() }
    }
```

**RecycleView Adapter**

```kotlin
class ToDoActivity : AppCompatActivity() {
 ...
    class RecyclerViewAdapter() :
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

            var data: List<ToDo> = listOf()

            fun setItems(dataSet: List<ToDo>) {
                data = dataSet
                // Notify adapter to reload dataset.
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
}
```

**Input Dialog**

```kotlin
fun initDialog(): Dialog {
        val linf = LayoutInflater.from(this)
        val inflator: View = linf.inflate(R.layout.alert_editor, null)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add ToDo")
            .setView(inflator)
            .setPositiveButton("CONFIRM", DialogInterface.OnClickListener(fun(dialog, id) {
                var title: Editable? = inflator.findViewById<EditText>(R.id.input_title).text
                var content: Editable? = inflator.findViewById<EditText>(R.id.input_content).text

                // insert value to database
                Thread {
                    db!!.todoDao().insert(
                    ToDo(
                        null,
                        title.toString(),
                        content.toString(),
                        Date().time,
                        Date().time
                    ))
                }.start()
            }))
            .setNegativeButton("CANCEL", DialogInterface.OnClickListener(fun(dialog, id) {
                //
            }))

        return builder.create()
    }
```

## LiveData

Create three files

- ViewModel: provide data to the UI and survive configuration changes.
- Factory: Geberate viewModel by repository.
- Repository: Provid data to view and view model.

First ypou have to turn List to **observable** object => use **Flow**

**DAO**

```kotlin
@Dao
interface ToDoDao {
    ...
    
    @Query("SELECT * FROM ToDo")
    fun getAll(): Flow<List<ToDo>>
    
    ...
}
```

### ViewModel

```kotlin
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ToDoViewModel(private val repository: ToDoRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<ToDo>> = repository.allToDo.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(toDo: ToDo) = viewModelScope.launch {
        repository.insert(toDo)
    }
}
```


### Factory

Rrovide viewmodel to UI

```kotlin
class ToDoViewModelFactory(private val repository: ToDoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToDoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

### Repository

```kotlin
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
        // It is important part the deal data operation in difference thread.
        AppDatabase.databaseWriteExecutor.execute(fun () {
            toDoDao.insert(toDo)
        })
    }
}

```

### Apply to view

1. Get **instance** in activity.

```kotlin
class ToDoActivity : AppCompatActivity() {
    ...    
    private val todoViewModel: ToDoViewModel by viewModels {
        ToDoViewModelFactory((application as ToDoApplication).repository)
    }
    ...
}
```

2. Observe data in **onCreate** function.

Reset list in adapter when data change.

```kotlin  
  override fun onCreate(savedInstanceState: Bundle?) {
    ...    
    todoViewModel.allWords.observe(owner = this) { todos ->
        // Update the cached copy of the words in the adapter.
        todos.let { rv_adapter.setItems(it) }
    }
    ...
  }
```

3. Change insert method of EditDialog

Insert data without Thread.

```kotlin
todoViewModel.insert(
    ToDo(
            null,
            title.toString(),
            content.toString(),
            Date().time,
            Date().time
        )
    )
```

## refs.

SQLite datatype

https://developer.android.com/reference/androidx/room/ColumnInfo

Extent LiveData to Room

https://developer.android.com/topic/libraries/architecture/livedata

https://developer.android.com/codelabs/android-room-with-a-view-kotlin

https://developer.android.com/jetpack/androidx/releases/lifecycle
