package com.example.storyapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.model.UserEntity

@Dao
interface UserStoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllStories(stories: List<UserEntity>)

    @Query("SELECT * FROM story")
    fun getAllStories(): LiveData<List<UserEntity>>


    @Query("DELETE FROM story")
    fun deleteAllStories()
}