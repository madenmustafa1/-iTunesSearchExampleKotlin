package com.example.itunessearchexample.model.search_response

data class SearchResponseModel(
    val resultCount: Int?,
    val results: List<Result?>?
)