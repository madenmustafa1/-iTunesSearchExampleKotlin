package com.example.itunessearchexample.view.main_page

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itunessearchexample.R
import com.example.itunessearchexample.adapter.AdapterClickListener
import com.example.itunessearchexample.adapter.SearchListRecyclerAdapter
import com.example.itunessearchexample.databinding.ActivityMainBinding
import com.example.itunessearchexample.extensions.getFilePath
import com.example.itunessearchexample.model.search_response.Result
import com.example.itunessearchexample.util.ChipQueryValue
import com.example.itunessearchexample.view.detail_page.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchListRecyclerAdapter: SearchListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()

        observeData()
    }

    private fun getSearch(searchQueryType: String) {
        mainActivityViewModel.getSearch(
            path = this.getFilePath(),
            searchQueryType = searchQueryType
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val myActionMenuItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = myActionMenuItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE;

        searchView.setOnQueryTextListener(mainActivityViewModel.searchTextListener())
        return true
    }

    private fun observeData() {
        mainActivityViewModel.searchResultLiveData.observe(this) {
            it.results?.let { data ->
                if (data.isNotEmpty()) {
                    recyclerViewSetVisibility(View.VISIBLE)
                    binding.animationView.visibility = View.GONE
                    binding.searchInfoText.visibility = View.GONE
                    searchListRecyclerAdapter.updateList(data.filterNotNull(), clearData = true)
                } else playNotFoundAnim()
            } ?: run { playNotFoundAnim() }
        }
    }

    private fun initView() {
        mainActivityViewModel.pathRoute = this.getFilePath()

        searchListRecyclerAdapter = SearchListRecyclerAdapter(arrayListOf(), adapterClickListener())
        binding.searchListRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.searchListRecyclerView.adapter = searchListRecyclerAdapter

        binding.moviesChip.setOnClickListener { getSearch(ChipQueryValue.MOVIES) }
        binding.musicChip.setOnClickListener { getSearch(ChipQueryValue.MUSIC) }
        binding.eBookChip.setOnClickListener { getSearch(ChipQueryValue.E_BOOK) }
        binding.podcastChip.setOnClickListener { getSearch(ChipQueryValue.PODCAST) }
    }

    private var animActive = true
    private fun playNotFoundAnim() {
        if (binding.searchListRecyclerView.visibility == View.GONE && !animActive) return
        animActive = false

        binding.searchInfoText.text = getString(R.string.not_found)
        recyclerViewSetVisibility(View.GONE)
        binding.animationView.visibility = View.VISIBLE
        binding.searchInfoText.visibility = View.VISIBLE
        binding.animationView.setAnimation(R.raw.not_found)
        binding.animationView.playAnimation()
    }

    private fun recyclerViewSetVisibility(visibility: Int) {
        if (binding.searchListRecyclerView.visibility == visibility) return
        binding.searchListRecyclerView.visibility = visibility
    }

    private fun adapterClickListener(): AdapterClickListener {
        return object : AdapterClickListener {
            override fun clickListener(item: Result) {
                Intent(this@MainActivity, DetailActivity::class.java).apply {
                    putExtra("json", mainActivityViewModel.modelToJson(item))
                    startActivity(this)
                }
            }

            override fun lastItem(lastItem: Boolean) {
                val arr = mainActivityViewModel.fakePagination()
                if (arr.isNotEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(2000)
                        CoroutineScope(Dispatchers.Main).launch {
                            searchListRecyclerAdapter.updateList(arr)
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}