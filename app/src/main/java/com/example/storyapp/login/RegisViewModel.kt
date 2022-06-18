package com.example.storyapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.model.RepositoryUser
import com.example.storyapp.retrofit.Injection

class RegisViewModel(private val userRepository: RepositoryUser) : ViewModel() {
    fun registerUser(
        name: String,
        email: String,
        password: String
    ) = userRepository.registerUser(name, email, password)

    class RegisViewModelFactory private constructor(private val userRepository: RepositoryUser) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisViewModel::class.java)) {
                return RegisViewModel(userRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: RegisViewModelFactory? = null

            fun getInstance(): RegisViewModelFactory = instance ?: synchronized(this) {
                instance ?: RegisViewModelFactory(Injection.provideUserRepository())
            }
        }
    }
}