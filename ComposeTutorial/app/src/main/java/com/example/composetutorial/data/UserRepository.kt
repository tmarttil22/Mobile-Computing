package com.example.composetutorial.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository (private val userDao: UserDao){
    suspend fun insertUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.insert(user)
        }
    }

    suspend fun insertPicture(picture: Picture) {
        withContext(Dispatchers.IO) {
            userDao.insertPicture(picture)
        }
    }

    suspend fun insertMessage(message: Msg) {
        withContext(Dispatchers.IO) {
            userDao.insertMessage(message)
        }
    }

    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }

    suspend fun updatePicture(picture: Picture) {
        withContext(Dispatchers.IO) {
            userDao.updatePicture(picture)
        }
    }

    fun getUser(): Flow<User?> = userDao.getUser()
    fun getPicture(): Flow<Picture?> = userDao.getPicture()

    fun getMessage(): Flow<Msg?> = userDao.getMessage()
    fun getAllMessages(): Flow<List<Msg?>> = userDao.getAllMessages()
}