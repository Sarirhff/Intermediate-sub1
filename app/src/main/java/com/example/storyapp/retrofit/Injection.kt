package com.example.storyapp.retrofit

import android.content.Context
import com.example.storyapp.database.AppExecutors
import com.example.storyapp.database.DatabaseUserStory
import com.example.storyapp.model.RepositoryUser
import com.example.storyapp.model.StoryReposUser

object Injection {

    fun provideUserRepository(): RepositoryUser {
        val apiService = ApiConfig.getApiService()
        return RepositoryUser.getInstance(apiService)
    }

    fun provideStoryRepository(context: Context): StoryReposUser {
        val apiService = ApiConfig.getApiService()
        val storyDatabase = DatabaseUserStory.getInstance(context)
        val storyDao = storyDatabase.storyUserDao()
        val appExecutors = AppExecutors()
        return StoryReposUser.getInstance(apiService, storyDao, appExecutors)
    }
}