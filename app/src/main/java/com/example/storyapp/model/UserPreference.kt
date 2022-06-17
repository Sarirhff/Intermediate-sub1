package com.example.storyapp.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    private val token = stringPreferencesKey("token")
    private val newUser = booleanPreferencesKey("new user")

    fun getUser(): Flow<String> {
        return dataStore.data.map {
            it[token] ?: "null"
        }
    }

    fun newUser(): Flow<Boolean> {
        return dataStore.data.map {
            it[this.newUser] ?: true
        }
    }

    suspend fun saveUser(token: String) {
        dataStore.edit {
            it[this.token] = token
        }
    }

    suspend fun saveNewUser(firstTime: Boolean) {
        dataStore.edit {
            it[this.newUser] = firstTime
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it[token] = "null"
        }
    }

    companion object {
        @Volatile
        private var instance: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference =
            instance ?: synchronized(this) {
                instance ?: UserPreference(dataStore)
            }.also { instance = it }
    }
}