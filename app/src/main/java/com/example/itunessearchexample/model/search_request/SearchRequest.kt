package com.example.itunessearchexample.model.search_request


data class SearchRequest(
    val term: String? = null,
    val limit: Int = 20,
    val entity: String? = null,
)
