package ru.netology.nmedia.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.R
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(
            { viewModel.likeById(it.id.toLong()) },
            { viewModel.shareById(it.id.toLong()) },
            { viewModel.removeById(it.id.toLong()) }
        )
        binding.list?.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        binding.save?.setOnClickListener {
            val content = binding.content.text?.toString().orEmpty()

            if (content.isBlank()) {
                Toast.makeText(
                    this,
                    R.string.content_is_blank_error,
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            viewModel.savePost(content)
            binding.content.setText("")
            binding.content.clearFocus()

            AndroidUtils.hideKeyboard(binding.content)
        }
    }
}