package com.example.itunessearchexample.extensions

import android.annotation.SuppressLint
import com.google.gson.Gson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

inline fun <reified T> String.stringToModel(): T? {
    val gson = Gson()
    return gson.fromJson<T>(this, T::class.java)
}

@SuppressLint("SimpleDateFormat")
fun String?.simplifyDate(): String {
    if (this == null) return ""
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date: Date? = format.parse(this)
        SimpleDateFormat("yyyy-MM-dd").format(date)
    } catch (e: ParseException) {
        this
    }
}