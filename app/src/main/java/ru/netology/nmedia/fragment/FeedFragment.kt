package ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        val adapter = PostsAdapter(
            likeListener = { viewModel.likeById(it.id) },
            shareListener = { post ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }
                startActivity(Intent.createChooser(intent, getString(R.string.description_post_share)))
                viewModel.shareById(post.id)
            },
            removeListener = { viewModel.removeById(it.id) },
            editListener = { post ->
                val bundle = Bundle().apply {
                    putLong("postId", post.id)
                    putString("content", post.content)
                }
                navController.navigate(R.id.action_feedFragment_to_newPostFragment, bundle)
            },
            postClickListener = { post ->
                val bundle = Bundle().apply {
                    putLong("postId", post.id)
                }
                navController.navigate(R.id.action_feedFragment_to_postFragment, bundle)
            },
            navController = navController
        )

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}