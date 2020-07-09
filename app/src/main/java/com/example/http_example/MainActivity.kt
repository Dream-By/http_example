package com.example.http_example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private const val ENDPOINT = "http://10.0.2.2:3000"  // Im using json-server running on my localhost and emulator
private const val BOOKS_URI = "/books"
private const val TITLE = "title"
var books = mutableListOf<String>()

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
            booksforDel.putStringArrayList("books", books as ArrayList<String>)
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
            return
        }
        val streamReader = InputStreamReader(httpUrlConnection.inputStream)
        var text: String = ""
        streamReader.use {
            text = it.readText()
        }

        //var books = mutableListOf<String>()
        val json = JSONArray(text)
        for (i in 0 until json.length()) {
            val jsonBook = json.getJSONObject(i)
            val title = jsonBook.getString(TITLE)
            books.add(title)
        }
        httpUrlConnection.disconnect()

        Handler(Looper.getMainLooper()).post {
            textView.text = books.reduce { acc, s -> "$acc\n$s" }
        }
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

    fun okClicked(selectedItem: String) {
        Toast.makeText(getApplicationContext(), "Choose the Button OK - " + selectedItem,Toast.LENGTH_LONG).show();
    }




}
