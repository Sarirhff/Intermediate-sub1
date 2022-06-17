package com.example.storyapp.database

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors {
    val diskIO: Executor = Executors.newSingleThreadExecutor()
}