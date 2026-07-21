package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.concurrent.thread
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryApiImpl

data class FeedState(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryApiImpl()

    private val _data = MutableLiveData(FeedState())
    val data: LiveData<FeedState> = _data

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedState(loading = true)
        thread {
            try {
                val posts = repository.getAll()
                _data.postValue(
                    FeedState(
                        posts = posts,
                        empty = posts.isEmpty()
                    )
                )
            } catch (e: Exception) {
                _data.postValue(
                    FeedState(
                        error = true,
                        posts = _data.value?.posts ?: emptyList()
                    )
                )
            }
        }
    }

    fun likeById(id: Long) {
        thread {
            try {
                repository.likeById(id)
                loadPosts()
            } catch (e: Exception) {
                _data.postValue(
                    _data.value?.copy(error = true)
                )
            }
        }
    }

    fun shareById(id: Long) {
        thread {
            try {
                loadPosts()
            } catch (e: Exception) {
                _data.postValue(
                    _data.value?.copy(error = true)
                )
            }
        }
    }

    fun removeById(id: Long) {
        thread {
            try {
                repository.removeById(id)
                loadPosts()
            } catch (e: Exception) {
                _data.postValue(
                    _data.value?.copy(error = true)
                )
            }
        }
    }

    fun updatePost(id: Long, content: String) {
        thread {
            try {
                repository.updatePost(id, content)
                _postCreated.postValue(Unit)
                loadPosts()
            } catch (e: Exception) {
                _data.postValue(
                    _data.value?.copy(error = true)
                )
            }
        }
    }

    fun savePost(content: String) {
        thread {
            try {
                val post = Post(
                    id = 0L,
                    author = "Me",
                    content = content,
                    published = "now",
                    likes = 0,
                    likedByMe = false,
                    shares = 0
                )
                repository.save(post)
                _postCreated.postValue(Unit)
                loadPosts()
            } catch (e: Exception) {
                _data.postValue(
                    _data.value?.copy(error = true)
                )
            }
        }
    }
}