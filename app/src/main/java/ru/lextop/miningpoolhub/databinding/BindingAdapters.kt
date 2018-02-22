package ru.lextop.miningpoolhub.databinding

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

@BindingAdapter("coinImg")
fun coinImg(imageView: ImageView, coin: String?) {
    if (coin == null) return
    Glide.with(imageView)
        .load("file:///android_asset/currency/${coin.toLowerCase()}.png")
        .into(imageView)
}
