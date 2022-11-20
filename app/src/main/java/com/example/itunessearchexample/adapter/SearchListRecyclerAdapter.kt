package com.example.itunessearchexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.itunessearchexample.R
import com.example.itunessearchexample.extensions.downloadImg
import com.example.itunessearchexample.extensions.simplifyDate
import com.example.itunessearchexample.model.search_response.Result
import java.util.*

class SearchListRecyclerAdapter(
    private val dataSet: ArrayList<Result>,
    private val listener: AdapterClickListener
) :
    RecyclerView.Adapter<SearchListRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artWorkImg: ImageView
        val collectionName: TextView
        val price: TextView
        val releaseDate: TextView

        init {
            artWorkImg = itemView.findViewById(R.id.itemArtWorkImg)
            collectionName = itemView.findViewById(R.id.itemCollectionName)
            price = itemView.findViewById(R.id.itemPrice)
            releaseDate = itemView.findViewById(R.id.itemReleaseDate)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_search_list, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val model = dataSet[position]
        with(viewHolder) {
            price.text = model.collectionPrice?.toString() ?: ""
            releaseDate.text = model.releaseDate.simplifyDate()
            if (model.collectionName.isNullOrEmpty()) collectionName.text = "No name"
            else collectionName.text = model.collectionName

            artWorkImg.downloadImg(model.artworkUrl100)

            itemView.setOnClickListener {
                listener.clickListener(model)
            }
        }

        if (position == (itemCount - 1)) {
            listener.lastItem(true)
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateList(model: List<Result>, clearData: Boolean = false) {
        if (clearData) dataSet.clear()
        dataSet.addAll(model)
        notifyDataSetChanged()
    }
}