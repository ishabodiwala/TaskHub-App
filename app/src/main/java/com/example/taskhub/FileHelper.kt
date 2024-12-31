package com.example.taskhub

import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.taskhub.model.Task
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

const val FILE_NAME = "todolist.dat"

fun writeData(items: SnapshotStateList<Task>, context: Context) {
    val fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
    val oas = ObjectOutputStream(fos)
    val itemList = ArrayList<Task>()
    itemList.addAll(items)
    oas.writeObject(itemList)
    oas.close()

}

fun readData(context: Context): SnapshotStateList<Task> {
    val items = SnapshotStateList<Task>()
    try {
        val itemList: ArrayList<Task>
        val fis = context.openFileInput(FILE_NAME)
        val ois = ObjectInputStream(fis)
        itemList = ois.readObject() as ArrayList<Task>
        items.addAll(itemList)
    } catch (e: FileNotFoundException) {
        Log.e("TAG", "File not found ")
    }

    return items
}