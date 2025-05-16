package com.example.redcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redcard.data.UserEntity
import com.example.redcard.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow<UserEntity?>(null)
    val loginResult: StateFlow<UserEntity?> = _loginResult.asStateFlow()

    private val _registerSuccess = MutableStateFlow<Boolean>(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()


    fun registerUser(user: UserEntity) {
        viewModelScope.launch {
            val trimmedEmail = user.email.trim()
            val trimmedPhone = user.phoneNumber.trim()

            val emailExists = repository.isEmailExists(trimmedEmail)
            val phoneExists = repository.isPhoneExists(trimmedPhone)

            if (emailExists) {
                _errorMessage.value = "Duplicate email!"
            } else if (phoneExists) {
                _errorMessage.value = "The contact number is duplicate!"
            } else {
                repository.registerUser(user.copy(email = trimmedEmail, phoneNumber = trimmedPhone, password = user.password.trim()))
                _registerSuccess.value = true
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()
            val user = repository.loginUser(trimmedEmail, trimmedPassword)
            if (user != null) {
                _loginResult.value = user
                _currentUser.value = user
            } else {
                _errorMessage.value = "The login information is incorrect!"
                _currentUser.value = null
            }
        }
    }

    fun logoutUser() {
        _currentUser.value = null
        _loginResult.value = null
    }

    fun clearMessages() {
        _errorMessage.value = null
        _registerSuccess.value = false
        _loginResult.value = null
    }
}