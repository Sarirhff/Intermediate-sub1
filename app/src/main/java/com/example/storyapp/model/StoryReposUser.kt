package com.example.storyapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.storyapp.database.AppExecutors
import com.example.storyapp.database.UserStoryDao
import com.example.storyapp.response.AddNewStoryResponse
import com.example.storyapp.response.GetAllStoriesResponse
import com.example.storyapp.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryReposUser private constructor(
    private val apiService: ApiService,
    private val storyUserDao: UserStoryDao,
    private val appExecutors: AppExecutors
    ) {
    private val getStoriesResult = MediatorLiveData<Result<List<UserEntity>>>()
    private val postNewStoryResult = MediatorLiveData<Result<AddNewStoryResponse>>()

    fun getStories(token: String): LiveData<Result<List<UserEntity>>> {
        getStoriesResult.value = Result.Loading
        val client = apiService.getStories(token)
        client.enqueue(object : Callback<GetAllStoriesResponse> {
            override fun onResponse(
                call: Call<GetAllStoriesResponse>,
                response: Response<GetAllStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val allStories = response.body()?.listStory
                    val listStory = ArrayList<UserEntity>()

                    appExecutors.diskIO.execute {
                        allStories?.forEach {
                            val story = UserEntity(
                                it?.id.toString(),
                                it?.photoUrl,
                                it?.name,
                                it?.description,
                                it?.createdAt

                            )
                            listStory.add(story)
                        }
                        storyUserDao.deleteAllStories()
                        storyUserDao.insertAllStories(listStory)

                    }
                } else {
                    Log.e(TAG, "Failed: Get stories response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                getStoriesResult.value = Result.Error(t.message.toString())
                Log.e(TAG, "Failed: Get All Stories response failure - ${t.message.toString()}")
            }
        })
        val dataUser = storyUserDao.getAllStories()
        getStoriesResult.addSource(dataUser) {
            getStoriesResult.value = Result.Success(it)
        }
        return getStoriesResult
    }

    fun addNewStory(
        token: String,
        imageFile: File,
        description: String
    ): LiveData<Result<AddNewStoryResponse>> {
        postNewStoryResult.postValue(Result.Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val descRequestBody = description.toRequestBody(textPlainMediaType)
        val imgMediaType = "image/jpg/jpeg".toMediaTypeOrNull()
        val imgMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            imageFile.asRequestBody(imgMediaType)
        )
        val client = apiService.postStory(token, imgMultiPart, descRequestBody)
        client.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val newStoryResponse = response.body()
                    if (newStoryResponse != null) {
                        postNewStoryResult.postValue(Result.Success(newStoryResponse))
                    } else {
                        postNewStoryResult.postValue(Result.Error(ADDING_ERROR))
                        Log.e(TAG, "Failed: story post info is null")
                    }
                } else {
                    postNewStoryResult.postValue(Result.Error(ADDING_ERROR))
                    Log.e(TAG,
                        "Failed: Add New Story response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                postNewStoryResult.postValue(Result.Error(ADDING_ERROR))
                Log.e(TAG, "Failed: Add New Story is null - ${t.message.toString()}")
            }
        })

        return postNewStoryResult
    }

    companion object {
        private val TAG = StoryReposUser::class.java.simpleName
        private const val ADDING_ERROR = "Story failed to upload, please try again later."

        @Volatile
        private var instance: StoryReposUser? = null

        fun getInstance(
            apiService: ApiService,
            storyDao: UserStoryDao,
            appExecutor: AppExecutors
        ): StoryReposUser =
            instance ?: synchronized(this) {
                instance ?: StoryReposUser(apiService, storyDao, appExecutor)
            }.also { instance = it }
    }
}
