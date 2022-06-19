package com.example.storyapp.retrofit

import com.example.storyapp.response.AddNewStoryResponse
import com.example.storyapp.response.GetAllStoriesResponse
import com.example.storyapp.response.LoginResponse
import com.example.storyapp.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Call<GetAllStoriesResponse>

    @Multipart
    @POST("stories")
    fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddNewStoryResponse>

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>
}