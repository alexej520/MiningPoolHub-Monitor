package ru.lextop.miningpoolhub.databinding

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.lextop.miningpoolhub.api.CoinmarketcapApi

@BindingAdapter("imgUrl")
fun imgUrl(imageView: ImageView, imgUrl: String?) {
    if (imgUrl == null) return
    Glide.with(imageView)
        .load(imgUrl)
        .into(imageView)
}

@BindingAdapter("coinImgUrl")
fun coinImgUrl(imageView: ImageView, imgUrl: String?) {
    if (imgUrl == null) return
    Glide.with(imageView)
        .load(CoinmarketcapApi.coinImageUrl(imgUrl, CoinmarketcapApi.SIZE_64x64))
        .into(imageView)
}
