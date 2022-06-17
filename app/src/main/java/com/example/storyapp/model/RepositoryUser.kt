package com.example.storyapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.storyapp.model.Result
import com.example.storyapp.response.LoginResponse
import com.example.storyapp.response.RegisterResponse
import com.example.storyapp.retrofit.ApiServis
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryUser private constructor(private val apiServis: ApiServis
    ) {
    private val loginResult = MediatorLiveData<Result<LoginResponse>>()
    private val regisResult = MediatorLiveData<Result<RegisterResponse>>()

    fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> {
        loginResult.value = Result.Loading
        val client = apiServis.login(
            email, password
        )

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && !loginResponse.error!!) {
                        loginResult.value = Result.Success(loginResponse)
                    } else {
                        loginResult.value = Result.Error(LOGIN_ERROR)
                        Log.e(TAG, "Failed: Login Response is failure")
                    }
                } else {
                    loginResult.value = Result.Error(LOGIN_ERROR)
                    Log.e(TAG, "Failed: Response Unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(LOGIN_ERROR)
                Log.e(TAG, "Failed: Can't get a response - ${t.message.toString()}")
            }

        })
        return loginResult
    }

    fun registerUser(username: String, email: String, password: String
    ): LiveData<Result<RegisterResponse>> {
        regisResult.value = Result.Loading
        val client = apiServis.register(
            username, email, password
        )

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    if (signUpResponse != null && !signUpResponse.error!!) {
                        regisResult.value = Result.Success(signUpResponse)
                    } else {
                        regisResult.value = Result.Error(REGIS_ERROR)
                        Log.e(TAG, "Failed: Register Response is failure")
                    }
                } else {
                    regisResult.value = Result.Error(REGIS_ERROR)
                    Log.e(TAG, "Failed: Response Unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                regisResult.value = Result.Error(REGIS_ERROR)
                Log.e(TAG, "Failed: Can't get a response - ${t.message.toString()}")
            }

        })
        return regisResult
    }

    companion object {
        private val TAG = RepositoryUser::class.java.simpleName
        private const val LOGIN_ERROR = "Sorry! You can't Login, please try again later."
        private const val REGIS_ERROR = "Sorry! You can't Sign up, please try again later."

        @Volatile
        private var instance: RepositoryUser? = null

        fun getInstance(apiService: ApiServis) =
            instance ?: synchronized(this) {
                instance ?: RepositoryUser(apiService)
            }.also { instance = it }
    }
}