package ru.netology.nmedia.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nmedia.R

fun ImageView.load(url: String)
{
    Glide.with(this).load(url)
        .placeholder(R.mipmap.ic_avatar_placeholder)
        .timeout(10_000)
        .error(R.mipmap.ic_avatar_placeholder)
        .into(this)
}

fun ImageView.loadAvatar(url: String)
{
    Glide.with(this).load(url)
        .placeholder(R.mipmap.ic_avatar_placeholder)
        .timeout(10_000)
        .apply(RequestOptions().circleCrop())
        .error(R.mipmap.ic_avatar_placeholder)
        .into(this)
}