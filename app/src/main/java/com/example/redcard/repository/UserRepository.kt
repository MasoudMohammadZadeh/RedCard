package com.example.redcard.repository

import com.example.redcard.data.UserDao
import com.example.redcard.data.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun loginUser(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
    }

    suspend fun isEmailExists(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }

    suspend fun isPhoneExists(phone: String): Boolean {
        return userDao.getUserByPhone(phone) != null
    }
}
