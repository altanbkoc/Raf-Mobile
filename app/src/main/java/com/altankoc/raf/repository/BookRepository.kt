package com.altankoc.raf.repository

import com.altankoc.raf.model.Book
import com.altankoc.raf.roomdb.BookDao
import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {
    suspend fun insertBook(book: Book) {
        bookDao.insertBook(book)
    }

    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }

    fun getAllBooks(): Flow<List<Book>> {
        return bookDao.getAllBooks()
    }

    fun getReadBooks(): Flow<List<Book>> {
        return bookDao.getReadBooks()
    }

    fun getUnreadBooks(): Flow<List<Book>> {
        return bookDao.getUnreadBooks()
    }

    fun getFavoriteBooks(): Flow<List<Book>> {
        return bookDao.getFavoriteBooks()
    }

    suspend fun getBookByTitle(title: String): Book? {
        return bookDao.getBookByTitle(title)
    }

    suspend fun getBookById(id: Int): Book? {
        return bookDao.getBookById(id)
    }
} 