package com.fmspcoding.notesapp.core.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fmspcoding.notesapp.domain.model.StorageImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


fun saveImageToIntervalStorage(filename: String, bitmap: Bitmap, context: Context): Boolean {
    return try {
        context.openFileOutput("$filename.png", MODE_PRIVATE).use { stream ->
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)) {
                throw IOException("Couldn't save bitmap.")
            }
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

suspend fun loadImageFromInternalStorage(filename: String, context: Context): StorageImage {
    return withContext(Dispatchers.IO) {
        val file = File(context.filesDir, filename)
        if (file.exists()) {
            val bytes = file.readBytes()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            StorageImage(file.name, bmp)
        } else {
            StorageImage("", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        }
    }
}

fun deleteImageFromInternalStorage(filename: String, context: Context): Boolean {
    return try {
        context.deleteFile(filename)
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

fun generateRandomName() : String {
    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val STRING_LENGTH = 10
    return (1..STRING_LENGTH)
        .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}


