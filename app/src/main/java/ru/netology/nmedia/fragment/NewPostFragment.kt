package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getLong("postId", 0L) ?: 0L
        val postContent = arguments?.getString("content")

        if (postId != 0L && !postContent.isNullOrBlank()) {
            binding.edit.setText(postContent)
        }

        AndroidUtils.showKeyboard(binding.edit)

        binding.ok.setOnClickListener {
            val content = binding.edit.text.toString()
            if (content.isBlank()) {
                Snackbar.make(binding.root, R.string.content_is_blank_error, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (postId != 0L) {
                viewModel.updatePost(postId, content)
            } else {
                viewModel.savePost(content)
            }
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}