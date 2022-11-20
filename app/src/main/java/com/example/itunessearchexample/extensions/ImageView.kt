package com.example.itunessearchexample.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.downloadImg(downloadUrl: String?) {
    Glide.with(this.context)
        .load(downloadUrl)
        .into(this)
}