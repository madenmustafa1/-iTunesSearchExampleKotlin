package com.example.itunessearchexample.extensions

import android.content.Context
import android.os.Environment

fun Context.getFilePath(): String {
    return getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        .toString() + "/model" + ".txt"
}