package com.example.composetutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch

class APIViewModel : ViewModel() {
    private val _dogImageUrl = MutableStateFlow<String?>(null)
    val dogImageUrl: StateFlow<String?> = _dogImageUrl

    init {
        fetchRandomDog()
    }

    fun fetchRandomDog() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = dogApi.getRandomDog().firstOrNull()
                _dogImageUrl.value = response?.url
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}