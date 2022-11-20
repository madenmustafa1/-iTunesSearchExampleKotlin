package com.example.itunessearchexample.repo

import com.example.itunessearchexample.model.search_request.SearchRequest
import com.example.itunessearchexample.service.ResponseData
import com.example.itunessearchexample.service.RetrofitServiceInterface
import okhttp3.ResponseBody
import java.lang.Exception
import javax.inject.Inject

class RetrofitRepo
@Inject constructor(private val apiService: RetrofitServiceInterface) {

    suspend fun getSearch(searchRequest: SearchRequest): ResponseData<ResponseBody> {
        return try {
            val response = apiService.search(
                limit = searchRequest.limit,
                entity = searchRequest.entity,
                term = searchRequest.term
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    return ResponseData.success(response.body())
                }
            }
            ResponseData.error("No result", null)
        } catch (e: Exception) {
            ResponseData.error("Error", null)
        }
    }

}