package com.example.composetutorial

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.Sensor.GyroScope
import com.example.composetutorial.Sensor.GyroSensor
import com.example.composetutorial.data.Msg
import com.example.composetutorial.data.Picture
import com.example.composetutorial.data.User
import com.example.composetutorial.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class UserViewModel (val userRepository: UserRepository) : ViewModel() {
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    private val _picture = mutableStateOf<Picture?>(null)
    val picture: State<Picture?> = _picture

    val latestMessage: StateFlow<String?> = userRepository.getMessage()
        .map { it?.msg } // Get the msg String out of the class
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val messageList: StateFlow<List<Msg?>> = userRepository.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

    // USER FUNCTIONS
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


    //PICTURE FUNCTIONS
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

    // MESSAGE FUNCTIONS:
    val messageFlow: StateFlow<Msg?> = userRepository.getMessage()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun insertMessage(message: Msg) {
        viewModelScope.launch {
            userRepository.insertMessage(message)
        }
    }

    fun getMessage() {
        viewModelScope.launch {
            userRepository.getMessage()
        }
    }
}