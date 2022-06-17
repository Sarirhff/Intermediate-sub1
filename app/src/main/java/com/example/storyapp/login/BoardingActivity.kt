package com.example.storyapp.login

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityBoardingBinding
import com.example.storyapp.model.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")
class BoardingActivity : AppCompatActivity() {

    private val binding: ActivityBoardingBinding by lazy {
        ActivityBoardingBinding.inflate(layoutInflater)
    }

    private val boardingViewModel: BoardingViewModel by viewModels {
        BoardingViewModel.BoardingViewModelFactory.getInstance(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonGetStart.setOnClickListener {
            val intent = Intent(this, LoginUserActivity::class.java)
            boardingViewModel.setNewUser(false)
            startActivity(intent)

            setupView()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}