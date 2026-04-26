package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.formatCount
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + v.paddingLeft, systemBars.top + v.paddingTop, systemBars.right + v.paddingRight, systemBars.bottom + v.paddingBottom)
            insets
        }

        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author?.text = post.author
                published?.text = post.published
                content?.text = post.content

                likeCount?.text = formatCount(post.likes)
                shareCount?.text = formatCount(post.shares)
                viewsCount?.text = formatCount(post.views)

                like?.setImageResource(if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24)
            }
        }

        binding.like?.setOnClickListener {
            viewModel.like()
        }

        binding.share?.setOnClickListener {
            viewModel.share()
        }
    }
}
