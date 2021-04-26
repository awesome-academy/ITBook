package com.example.itbook.data.source

import com.example.itbook.data.model.Book
import com.example.itbook.utils.OnDataLoadCallBack

interface BooksDataSource {
    interface Remote {
        fun getRemoteBooks(query: String, callback: OnDataLoadCallBack<List<Book>>)
    }

    interface Local {
        fun insertBook(book: Book, callback: OnDataLoadCallBack<Long>)
        fun getBook(isbn13: String, callback: OnDataLoadCallBack<Book>)
        fun getAllBooks(callback: OnDataLoadCallBack<List<Book>>)
        fun deleteBook(isbn13: String, callback: OnDataLoadCallBack<Int>)
    }
}