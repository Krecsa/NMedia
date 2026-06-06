package ru.netology.nmedia.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.formatCount
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var currentPost: Post? = null
    private var postId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getLong("postId", 0L) ?: 0L
        if (postId == 0L) {
            findNavController().navigateUp()
            return
        }

        // Наблюдаем за списком постов, чтобы найти наш пост
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            currentPost = posts.find { it.id == postId }
            currentPost?.let { bindPost(it) }
        }
    }

    private fun bindPost(post: Post) {
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = formatCount(post.likes)
            like.setOnClickListener {
                viewModel.likeById(post.id)
            }

            share.text = formatCount(post.shares)
            share.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }
                startActivity(Intent.createChooser(intent, getString(R.string.description_post_share)))
                viewModel.shareById(post.id)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                val bundle = Bundle().apply {
                                    putLong("postId", post.id)
                                    putString("content", post.content)
                                }
                                findNavController().navigate(R.id.action_postFragment_to_newPostFragment, bundle)
                                true
                            }
                            R.id.remove -> {
                                viewModel.removeById(post.id)
                                findNavController().popBackStack()
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
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
        }
    }

    private fun extractVideoUrl(text: String): String? {
        val regex = Regex("https?://(www\\.)?rutube\\.ru/video/\\S+")
        return regex.find(text)?.value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}