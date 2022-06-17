package com.example.storyapp.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.RepositoryUser
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.UserPreference
import com.example.storyapp.retrofit.Injection
import kotlinx.coroutines.launch

class RegisViewModel (private val userRepository: RepositoryUser) : ViewModel() {
    fun registerUser(
        username: String,
        email: String,
        password: String
    ) = userRepository.registerUser(username, email, password)
}
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