package com.example.connectify.data

data class Post(
    val description: String ="",
    val imageURI: String ="",
    val createdBy: User = User(),
    val createdAt: Long = 0L,
    val likedBy: ArrayList<String> = ArrayList()
)