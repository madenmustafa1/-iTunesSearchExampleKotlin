package com.example.itunessearchexample.service

data class ResponseData<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): ResponseData<T> {
            return ResponseData(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): ResponseData<T> {
            return ResponseData(Status.ERROR, data, msg)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR
}