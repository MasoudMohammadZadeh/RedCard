package com.example.redcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.redcard.repository.UserRepository

class UserViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(UserViewModel::class.java) ->
                UserViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
}
