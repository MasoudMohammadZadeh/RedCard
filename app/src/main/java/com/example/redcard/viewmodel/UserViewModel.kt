package com.example.redcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redcard.data.UserEntity
import com.example.redcard.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch


class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow<UserEntity?>(null)
    val loginResult: StateFlow<UserEntity?> = _loginResult

    private val _registerSuccess = MutableStateFlow<Boolean>(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage



    fun registerUser(user: UserEntity) {
        viewModelScope.launch {
            val emailExists = repository.isEmailExists(user.email)
            val phoneExists = repository.isPhoneExists(user.phoneNumber)

            if (emailExists) {
                _errorMessage.value = "Duplicate email!"
            } else if (phoneExists) {
                _errorMessage.value = "The contact number is duplicate!"
            } else {
                repository.registerUser(user)
                _registerSuccess.value = true
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.loginUser(email, password)
            if (user != null) {
                _loginResult.value = user
            } else {
                _errorMessage.value = "The login information is incorrect!"
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _registerSuccess.value = false
    }
}
