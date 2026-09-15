package com.example.plantillabcp.data.repository


import com.example.plantillabcp.data.local.dao.UserDao
import com.example.plantillabcp.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class LocalRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun addUser(name: String, email: String) {
        userDao.insertUser(UserEntity(name = name, email = email))
    }

    suspend fun deleteUser(user: UserEntity) {
        userDao.deleteUser(user)
    }

}