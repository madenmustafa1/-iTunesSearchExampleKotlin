package com.example.itunessearchexample.adapter

import com.example.itunessearchexample.model.search_response.Result

interface AdapterClickListener {
    fun clickListener(item: Result)
    fun lastItem(lastItem: Boolean)
}