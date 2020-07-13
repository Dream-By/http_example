package com.example.http_example

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment


class DelFragment : DialogFragment() {


    var selectedItem = 0


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val booksForDelete = getArguments()?.getStringArrayList("books")!!

            val builder = AlertDialog.Builder(activity)
            val checkedItem = -1

            builder.setTitle("Choose the book for DELETE")
                .setSingleChoiceItems(booksForDelete.toTypedArray(),checkedItem){
                    dialog, which ->
                    Toast.makeText(activity,"Choosen book: ${booksForDelete.toTypedArray()[which]}",Toast.LENGTH_SHORT).show()
                    selectedItem = which
                }
            builder.setPositiveButton("OK") { dialog, which ->
                Thread {
                 (activity as MainActivity?)?.delBook(selectedItem)            // user clicked OK
                }.start()
            }
            builder.setNegativeButton("Cancel", null)
            // user clicked Cancel
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}