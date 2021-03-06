package com.example.storyapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoardingViewModel (private val pref: UserPreference) : ViewModel() {

    fun saveNewUser(firstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            pref.saveNewUser(firstTime)
        }
    }

    class BoardingViewModelFactory private constructor(private val pref: UserPreference) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BoardingViewModel::class.java)) {
                return BoardingViewModel(pref) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: BoardingViewModelFactory? = null

            fun getInstance(userPreference: UserPreference): BoardingViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: BoardingViewModelFactory(userPreference)
                }.also { instance = it }
        }
    }
}