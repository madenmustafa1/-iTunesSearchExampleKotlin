package com.example.itunessearchexample.view.main_page


import android.text.TextUtils
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itunessearchexample.model.search_request.SearchRequest
import com.example.itunessearchexample.model.search_response.Result
import com.example.itunessearchexample.model.search_response.SearchResponseModel
import com.example.itunessearchexample.repo.RetrofitRepo
import com.example.itunessearchexample.util.AppMessages.DISK_MESSAGES
import com.example.itunessearchexample.util.AppMessages.GENERAL_MESSAGES
import com.example.itunessearchexample.util.ChipQueryValue
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.*
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repo: RetrofitRepo
    var pathRoute: String = ""
    private var _searchText: String? = null
    private var _searchQueryType = ChipQueryValue.MOVIES

    private val _generalErrorMessage = MutableLiveData<String?>()

    fun searchTextListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    _searchText = null
                    setSearchText()
                } else {
                    newText?.let {
                        _searchText = if (it.length > 2) it
                        else null
                        setSearchText()
                    }
                }
                return true
            }
        }
    }

    private fun setSearchText() {
        getSearch(
            path = pathRoute,
            searchText = _searchText,
            searchQueryType = _searchQueryType
        )
    }

    private val _searchResultMutableLiveData = MutableLiveData<SearchResponseModel>()
    val searchResultLiveData: LiveData<SearchResponseModel> = _searchResultMutableLiveData

    fun getSearch(path: String, searchText: String? = null, searchQueryType: String = "") {
        _searchQueryType = searchQueryType
        pathRoute = path
        _generalErrorMessage.value = null
        pageNumber = 1
        viewModelScope.launch {
            val result = repo.getSearch(
                SearchRequest(
                    entity = searchQueryType,
                    term = searchText ?: _searchText
                )
            )
            result.data?.let {
                writeResponseBodyToDisk(it)
            } ?: run {
                _generalErrorMessage.value = result.message ?: GENERAL_MESSAGES
            }
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody) {
        try {
            val file = File(pathRoute)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) break

                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                getFileToModel()
            } catch (e: IOException) {
                _generalErrorMessage.value = DISK_MESSAGES
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            _generalErrorMessage.value = DISK_MESSAGES
        }
    }

    private fun getFileToModel() {
        try {
            val file = File(pathRoute)
            val sb = StringBuilder()
            for (i in file.readLines()) {
                sb.append(i)
            }

            val gson = Gson()
            val model = gson.fromJson(sb.toString().trim(), SearchResponseModel::class.java)
            _searchResultMutableLiveData.value = model
        } catch (e: Exception) {
            _generalErrorMessage.value = DISK_MESSAGES
        }
    }

    fun modelToJson(model: Result): String {
        val gson = Gson()
        return gson.toJson(model)
    }

    private var pageNumber = 1
    fun fakePagination(maxSize: Int = 20): List<Result> {
        try {
            pageNumber++
            val arr = arrayListOf<Result>()
            _searchResultMutableLiveData.value.let {
                it?.results?.let { data ->
                    val nonNullData = data.filterNotNull()
                    if (nonNullData.size <= maxSize) return arrayListOf()
                    val getPageSize = (pageNumber * maxSize)
                    for (i in getPageSize..(getPageSize + maxSize)) {
                        if (nonNullData.size <= (i + 1)) return arr
                        arr.add(nonNullData[i - 1])
                    }
                }
            }
            return arr
        } catch (e: Exception) {
            return  arrayListOf()
        }
    }
}