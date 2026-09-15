package com.example.plantillabcp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantillabcp.data.local.entity.UserEntity
import com.example.plantillabcp.data.repository.LocalRepository

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoomViewModel(private val repository: LocalRepository) : ViewModel() {

    val usersState: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addUser(name: String, email: String) {
        if (name.isBlank() || email.isBlank()) return
        viewModelScope.launch {
            repository.addUser(name, email)
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }
}