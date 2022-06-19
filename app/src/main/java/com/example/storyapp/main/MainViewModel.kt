package com.example.storyapp.main

import android.content.Context
import androidx.lifecycle.*
import com.example.storyapp.model.StoryReposUser
import com.example.storyapp.model.UserPreference
import com.example.storyapp.retrofit.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreference: UserPreference,
    private val storyUserRepository: StoryReposUser) : ViewModel() {


    fun getStories(token: String) = storyUserRepository.getStories(token)

    fun checkIfTokenAvailable(): LiveData<String> {
        return userPreference.getUser().asLiveData()
    }
    fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreference.logout()
        }
    }

    class MainViewModelFactory(
        private val storyUserRepository: StoryReposUser,
        private val userPreference: UserPreference
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(userPreference, storyUserRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: MainViewModelFactory? = null

            fun getInstance(
                context: Context,
                userPreference: UserPreference
            ): MainViewModelFactory = instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(
                    Injection.provideStoryRepository(context),
                    userPreference
                )
            }.also { instance = it }
        }
    }
}