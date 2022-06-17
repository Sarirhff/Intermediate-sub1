package com.example.storyapp.login

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.example.storyapp.model.Result
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginUserBinding
import com.example.storyapp.main.MainActivity
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginUserBinding
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory.getInstance(
            UserPreference.getInstance(dataStore)
        )
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

    private fun setupAction() {
        binding.buttonLogin.setOnClickListener {
            if (!binding.edtEmailLogin.text.isNullOrEmpty() && !binding.edtPasswordLogin.text.isNullOrEmpty()) {
                val email = binding.edtEmailLogin.text.toString()
                val password = binding.edtPasswordLogin.text.toString()
                val result = loginViewModel.loginUser(email, password)

                result.observe(this) {
                    when (it) {
                        is Result.Loading -> {
                            binding.loadingBar.visibility = View.VISIBLE
                        }

                        is Result.Error -> {
                            binding.loadingBar.visibility = View.INVISIBLE
                            val error = it.error
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Success -> {
                            binding.loadingBar.visibility = View.INVISIBLE
                            val data = it.data
                            loginViewModel.saveUser(data.loginResult?.token!!)
                            Log.d("LoginActivity", "Token: ${data.loginResult.token}")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                binding.edtEmailLogin.error = resources.getString(R.string.empty_email)

                if (binding.edtPasswordLogin.text.isNullOrEmpty()) {
                    binding.edtPasswordLogin.error =
                        resources.getString(R.string.empty_pass)
                }
            }
        }
        binding.btnRegist.setOnClickListener {
            val intent = Intent(this, RegisUserActivity::class.java)
            startActivity(intent)
        }
    }
}