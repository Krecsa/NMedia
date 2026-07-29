package ru.netology.nmedia.dto

data class Post(
    val id: Long = 0,
    val author: String = "",
    val published: Long = 0,
    val content: String = "",
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val shares: Int = 0,
    val sharedByMe: Boolean = false,
    val views: Int = 0,
    val authorAvatar: String? = null,
    val attachment: Attachment? = null
)