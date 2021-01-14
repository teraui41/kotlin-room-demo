package com.example.todoapp.database

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


class ToDoViewModelFactory(private val repository: ToDoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToDoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}