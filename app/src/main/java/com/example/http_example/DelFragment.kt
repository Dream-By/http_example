package com.example.http_example

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment


class DelFragment : DialogFragment() {

    //private var BookNames = arrayOf<String>()
    //var booksForDelete = arrayListOf<String>()
    var selectedItem = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val booksForDelete = getArguments()?.getStringArrayList("books")!!
            var BookNames = booksForDelete.toTypedArray()
            val builder = AlertDialog.Builder(activity)
            val checkedItem = -1
            builder.setTitle("Choose the book for DELETE")
                .setSingleChoiceItems(BookNames,checkedItem){
                    dialog, which ->
                    Toast.makeText(activity,"Choosen book: ${BookNames[which]}",Toast.LENGTH_SHORT).show()
                    selectedItem = BookNames[which]
                }
            builder.setPositiveButton("OK") { dialog, which ->

                 (activity as MainActivity?)?.okClicked(selectedItem)            // user clicked OK
            }
            builder.setNegativeButton("Cancel", null)
            // user clicked Cancel
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}