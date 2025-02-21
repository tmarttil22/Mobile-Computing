package com.example.composetutorial

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.Sensor.GyroScope
import com.example.composetutorial.Sensor.GyroSensor
import com.example.composetutorial.data.Picture
import com.example.composetutorial.data.User
import com.example.composetutorial.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class UserViewModel (val userRepository: UserRepository) : ViewModel() {
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    private val _picture = mutableStateOf<Picture?>(null)
    val picture: State<Picture?> = _picture

    init {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                _user.value = user
            }
            userRepository.getPicture().collect { picture ->
                _picture.value = picture
            }
        }
    }

    val userFlow: StateFlow<User?> = userRepository.getUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun insertUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user)
        }
    }

    val pictureFlow: StateFlow<Picture?> = userRepository.getPicture()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun insertPicture(picture: Picture) {
        viewModelScope.launch {
            userRepository.insertPicture(picture)
        }
    }



    fun updatePicture(picture: Picture) {
        viewModelScope.launch {
            userRepository.updatePicture(picture)
        }
    }

    fun getPicture() {
        viewModelScope.launch {
            userRepository.getPicture()
        }
    }
}