package com.example.itunessearchexample.view.detail_page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.itunessearchexample.databinding.ActivityDetailBinding
import com.example.itunessearchexample.extensions.downloadImg
import com.example.itunessearchexample.extensions.simplifyDate
import com.example.itunessearchexample.extensions.stringToModel
import com.example.itunessearchexample.model.search_response.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var model: Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        intent.getStringExtra("json")?.let {
            model = it.stringToModel<Result>()
            model?.let {
                initView()
                return
            } ?: run { errorModel() }
        } ?: run { errorModel() }
    }

    private fun errorModel() {
        Toast.makeText(this@DetailActivity, "No content", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun initView() {
        with(model!!) {
            binding.artWorkImg.downloadImg(artworkUrl100)
            binding.collectionName.text = collectionName ?: "No Name"
            binding.price.text = collectionPrice.toString()
            binding.releaseDate.text = releaseDate.simplifyDate()
            binding.country.text = country
            binding.collectionArtistName.text = collectionArtistName
            binding.collectionId.text = collectionId.toString()
            binding.kind.text = kind
        }
        binding.backLayout.setOnClickListener { finish() }
        binding.cardView.setOnClickListener {  }
    }
}