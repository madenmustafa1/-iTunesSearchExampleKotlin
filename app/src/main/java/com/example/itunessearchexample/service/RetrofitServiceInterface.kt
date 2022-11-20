package com.example.itunessearchexample.service

import com.example.itunessearchexample.util.RetrofitURL.SEARCH
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitServiceInterface {
    @GET(SEARCH)
    suspend fun search(
        @Query("term") term: String? = null,
        @Query("limit") limit: Int = 25,
        @Query("entity") entity: String? = null,
    ): Response<ResponseBody>
}