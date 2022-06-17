package com.example.storyapp.login

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityProfileBinding
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ProfileActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.buttonLogout.setOnClickListener {
            profileViewModel.logout()
        }
    }
}