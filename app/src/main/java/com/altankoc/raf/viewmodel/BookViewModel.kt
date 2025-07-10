package com.altankoc.raf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.raf.model.Book
import com.altankoc.raf.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())
    val allBooks: StateFlow<List<Book>> = _allBooks.asStateFlow()

    private val _readBooks = MutableStateFlow<List<Book>>(emptyList())
    val readBooks: StateFlow<List<Book>> = _readBooks.asStateFlow()

    private val _unreadBooks = MutableStateFlow<List<Book>>(emptyList())
    val unreadBooks: StateFlow<List<Book>> = _unreadBooks.asStateFlow()

    private val _favoriteBooks = MutableStateFlow<List<Book>>(emptyList())
    val favoriteBooks: StateFlow<List<Book>> = _favoriteBooks.asStateFlow()

    init {
        observeBooks()
    }

    private fun observeBooks() {
        viewModelScope.launch {
            repository.getAllBooks().collect { _allBooks.value = it }
        }
        viewModelScope.launch {
            repository.getReadBooks().collect { _readBooks.value = it }
        }
        viewModelScope.launch {
            repository.getUnreadBooks().collect { _unreadBooks.value = it }
        }
        viewModelScope.launch {
            repository.getFavoriteBooks().collect { _favoriteBooks.value = it }
        }
    }

    fun insertBook(book: Book) {
        viewModelScope.launch {
            repository.insertBook(book)
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    suspend fun isBookExists(title: String): Boolean {
        return repository.getBookByTitle(title) != null
    }

    suspend fun getBookById(id: Int): Book? {
        return repository.getBookById(id)
    }
} 