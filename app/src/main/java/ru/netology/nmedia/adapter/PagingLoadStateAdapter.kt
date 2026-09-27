package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.ItemLoadingBinding

class PagingLoadStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<PagingLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): LoadStateViewHolder {
        val binding = ItemLoadingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return LoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadStateViewHolder(
        private val binding: ItemLoadingBinding,
        private val retry: () -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            binding.progress.isVisible = loadState is LoadState.Loading
            binding.retry.isVisible = loadState is LoadState.Error
            binding.retry.setOnClickListener {
                retry()
            }
        }
    }
}