package ru.netology.nmedia.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.formatCount
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

typealias LikeListener = (Post) -> Unit
typealias ShareListener = (Post) -> Unit
typealias RemoveListener = (Post) -> Unit
typealias EditListener = (Post) -> Unit
typealias PostClickListener = (Post) -> Unit

class PostsAdapter(
    private val likeListener: LikeListener,
    private val shareListener: ShareListener,
    private val removeListener: RemoveListener,
    private val editListener: EditListener,
    private val postClickListener: PostClickListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(
            binding,
            likeListener,
            shareListener,
            removeListener,
            editListener,
            postClickListener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val likeListener: LikeListener,
    private val shareListener: ShareListener,
    private val removeListener: RemoveListener,
    private val editListener: EditListener,
    private val postClickListener: PostClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        with(binding) {
            author.text = post.author

            val dateFormat = SimpleDateFormat("dd MMMM в HH:mm", Locale("ru"))
            dateFormat.timeZone = TimeZone.getTimeZone("UTC+3")
            published.text = dateFormat.format(Date(post.published * 1000))

            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = formatCount(post.likes)
            like.setOnClickListener {
                likeListener(post)
            }

            share.text = formatCount(post.shares)
            share.setOnClickListener {
                shareListener(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                editListener(post)
                                true
                            }
                            R.id.remove -> {
                                removeListener(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            if (!post.authorAvatar.isNullOrBlank()) {
                Glide.with(itemView.context)
                    .load("http://10.0.2.2:9999/avatars/${post.authorAvatar}")
                    .placeholder(R.drawable.ic_netology_48dp)
                    .error(R.drawable.ic_netology_48dp)
                    .transform(CircleCrop())
                    .into(avatar)
            } else {
                avatar.setImageResource(R.drawable.ic_netology_48dp)
            }

            val videoUrl = extractVideoUrl(post.content)
            if (videoUrl == null) {
                videoContainer.visibility = View.GONE
            } else {
                videoContainer.visibility = View.VISIBLE
                videoContainer.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                    it.context.startActivity(intent)
                }
            }

            if (post.attachment != null && post.attachment.type == "IMAGE") {
                attachmentContainer.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load("http://10.0.2.2:9999/images/${post.attachment.url}")
                    .placeholder(R.drawable.ic_play_video)
                    .override(48, 48)
                    .error(R.drawable.ic_error_100dp)
                    .into(attachmentImage)
            } else {
                attachmentContainer.visibility = View.GONE
            }

            root.setOnClickListener {
                postClickListener(post)
            }
        }
    }

    private fun extractVideoUrl(text: String): String? {
        val regex = Regex("https?://(www\\.)?rutube\\.ru/video/\\S+")
        return regex.find(text)?.value
    }
}