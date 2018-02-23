package ru.lextop.miningpoolhub.databinding

import android.databinding.BindingAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition

@BindingAdapter("bind:imgUrl", "crossFade", requireAll = false)
fun imgUrl(imageView: ImageView, imgUrl: String?, crossFade: Boolean) {
    if (imgUrl == null) return
    Glide.with(imageView)
        .load(imgUrl)
        .into(imageView)
}

@BindingAdapter("android:text", "bind:converter", requireAll = true)
fun <T>balance(textView: TextView, text: T, format: Format?) {


    if (text == null || format == null) return
    textView.text = format.format(text)
}