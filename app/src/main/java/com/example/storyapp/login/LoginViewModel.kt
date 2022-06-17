package com.example.storyapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.RepositoryUser
import com.example.storyapp.model.UserPreference
import com.example.storyapp.retrofit.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference, private val repos: RepositoryUser) :
    ViewModel() {

    fun saveUser(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            pref.saveUser(token)
        }
    }

    fun loginUser(email: String, password: String) =
        repos.loginUser(email, password)


}

class LoginViewModelFactory private constructor(
    private val userRepository: RepositoryUser,
    private val userPreference: UserPreference
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userPreference, userRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: LoginViewModelFactory? = null
        fun getInstance(
            userPreference: UserPreference
        ): LoginViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: LoginViewModelFactory(
                    Injection.provideUserRepository(),
                    userPreference
                )
            }
    }
}

