package ru.lextop.miningpoolhub.databinding

import android.databinding.BindingAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.text.DecimalFormat

@BindingAdapter("imgUrl")
fun imgUrl(imageView: ImageView, url: String?) {
    if (url == null) return
    Glide.with(imageView)
        .load(url)
        .into(imageView)
}

@BindingAdapter("balance")
fun balance(textView: TextView, balance: Double) {
    DecimalFormat("")
}