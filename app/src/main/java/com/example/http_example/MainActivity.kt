package com.example.http_example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.example.http_example.model.Book
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.reduce as reduce


private const val ENDPOINT = "http://10.0.2.2:3000"  // Im using json-server running on my localhost and emulator
private const val BOOKS_URI = "/books"
private const val TITLE = "title"
private const val ID = "id"
private const val TAG = "Message"



var books = mutableListOf<Book>()
var bookfordel = mutableListOf<String>()

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener{
            val book = editText.text
            Thread {
                addBook(book.toString())
            }.start()
        }
        Thread {
        getBooksAndShowIt()
        }.start()


        button2.setOnClickListener{
            val delFragment = DelFragment()
            val booksforDel = Bundle()
            booksforDel.putStringArrayList("books", bookfordel as ArrayList<String>)
            delFragment.setArguments(booksforDel)
            val manager = supportFragmentManager
            delFragment.show(manager,"Delete Book")
        }
    }

    @WorkerThread
    fun getBooksAndShowIt() {
        val httpUrlConnection = URL(ENDPOINT + BOOKS_URI).openConnection() as HttpURLConnection
        httpUrlConnection.apply {
            connectTimeout = 10000 // 10 seconds
            requestMethod = "GET"
            doInput = true
        }
        if (httpUrlConnection.responseCode != HttpURLConnection.HTTP_OK) {
            // show error toast
            Toast.makeText(this, "Response code is ${httpUrlConnection.responseCode}", Toast.LENGTH_SHORT).show()
            return
        }
        val streamReader = InputStreamReader(httpUrlConnection.inputStream)
        var text: String = ""
        streamReader.use {
            text = it.readText()
        }
        books.clear()
        bookfordel.clear()
        //var books = mutableListOf<String>()
        val json = JSONArray(text)
        for (i in 0 until json.length()) {
            val jsonBook = json.getJSONObject(i)
            val title = jsonBook.getString(TITLE)
            val id = jsonBook.getInt(ID)
            books.add(Book(title, id))
            bookfordel.add(title)
        }
        httpUrlConnection.disconnect()

        Handler(Looper.getMainLooper()).post {
            textView.text = books.map(Book::title).joinToString("\n")

        }
        editText.text.clear()
    }



    @WorkerThread
    fun addBook(book: String) {
        val httpUrlConnection = URL(ENDPOINT + BOOKS_URI).openConnection() as HttpURLConnection
        val body = JSONObject().apply {
            put(TITLE, book)
        }
        httpUrlConnection.apply {
            connectTimeout = 10000 // 10 seconds
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }
        OutputStreamWriter(httpUrlConnection.outputStream).use {
            it.write(body.toString())
        }

        httpUrlConnection.responseCode
        httpUrlConnection.disconnect()
        getBooksAndShowIt()
    }

    @WorkerThread
    fun delBook(selectedItem:Int) {
        var httpUrlConnection: HttpURLConnection? = null
        System.out.println("selectedItem - "+selectedItem)

        val id = books.map(Book::id).get(selectedItem)

        System.out.println("id - "+id)


        try {
            httpUrlConnection = URL(ENDPOINT + BOOKS_URI + "/${id}").openConnection() as HttpURLConnection
            httpUrlConnection.apply {
                connectTimeout = 10000 // 10 seconds
                requestMethod = "DELETE"
                setRequestProperty("Content-Type", "application/json")
            }
            httpUrlConnection.responseCode
        } catch (exc: Exception) {
            Log.e(TAG, "removeBook", exc)
        } finally {
            httpUrlConnection?.disconnect()
        }
        getBooksAndShowIt()
    }

    }

