package com.example.storyapp.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.StoryReposUser
import com.example.storyapp.model.UserPreference
import com.example.storyapp.retrofit.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel (private val userPreference: UserPreference,
                        private val storyUserRepository: StoryReposUser) : ViewModel() {

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreference.logout()
        }
    }

    class ProfileViewModelFactory(
        private val storyUserRepository: StoryReposUser,
        private val userPreference: UserPreference
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(userPreference, storyUserRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: ProfileViewModelFactory? = null

            fun getInstance(
                context: Context,
                userPreference: UserPreference
            ): ProfileViewModelFactory = instance ?: synchronized(this) {
                instance ?: ProfileViewModelFactory(
                    Injection.provideStoryRepository(context),
                    userPreference
                )
            }.also { instance = it }
        }
    }
}