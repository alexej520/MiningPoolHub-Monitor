package ru.lextop.miningpoolhub.databinding

import android.databinding.BindingAdapter
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import com.bumptech.glide.Glide

@BindingAdapter("bind:imgUrl", "crossFade", requireAll = false)
fun imgUrl(imageView: ImageView, imgUrl: String?, crossFade: Boolean) {
    if (imgUrl == null) return
    Glide.with(imageView)
        .load(imgUrl)
        .into(imageView)
}

@BindingAdapter("app:menuRes")
fun menuRes(toolbar: Toolbar, menuRes: Int) {
    if (menuRes == 0) return
    toolbar.inflateMenu(menuRes)
}
