package com.example.storyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapp.model.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1
)
abstract class DatabaseUserStory : RoomDatabase() {

    abstract fun storyUserDao(): UserStoryDao

    companion object {
        @Volatile
        private var instance: DatabaseUserStory? = null

        fun getInstance(context: Context): DatabaseUserStory =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, DatabaseUserStory::class.java, "stories.db")
                    .build()
            }.also { instance = it }
    }
}