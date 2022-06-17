package com.example.storyapp.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.storyapp.model.StoryReposUser
import com.example.storyapp.model.UserPreference
import com.example.storyapp.retrofit.Injection
import java.io.File

class UploadStoryViewModel (
    private val storyUserRepository: StoryReposUser,
    private val userPreference: UserPreference
) : ViewModel() {
    fun addNewStory(token: String, imageFile: File, description: String) =
        storyUserRepository.addNewStory(token, imageFile, description)

    fun checkIfTokenAvailable(): LiveData<String> {
        return userPreference.getUser().asLiveData()
    }
}
class UploadStoryViewModelFactory private constructor(
    private val storyUserRepository: StoryReposUser,
    private val userPreference: UserPreference
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadStoryViewModel::class.java)) {
            return UploadStoryViewModel(storyUserRepository, userPreference) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: UploadStoryViewModelFactory? = null

        fun getInstance(
            context: Context,
            userPreference: UserPreference
        ): UploadStoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: UploadStoryViewModelFactory(
                    Injection.provideStoryRepository(context),
                    userPreference
                )
            }.also { instance = it }
    }
}