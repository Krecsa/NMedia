package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SingleLiveEvent<T> : MutableLiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { value ->
            if (value != null) {
                observer.onChanged(value)
                super.postValue(null as T?)
            }
        }
    }

    override fun postValue(value: T) {
        super.postValue(value)
    }
}