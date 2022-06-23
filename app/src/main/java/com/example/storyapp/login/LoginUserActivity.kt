package com.example.storyapp.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginUserBinding
import com.example.storyapp.main.MainActivity
import com.example.storyapp.model.Result
import com.example.storyapp.model.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
class LoginUserActivity : AppCompatActivity() {

    private val binding: ActivityLoginUserBinding by lazy {
        ActivityLoginUserBinding.inflate(layoutInflater)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewModelFactory.getInstance(
            UserPreference.getInstance(dataStore)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAction()
        hideSystemUI()

    }

    override fun onResume() {
        super.onResume()
        checkNewUser()
    }

    private fun hideSystemUI() {
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

    private fun checkNewUser() {
        loginViewModel.checkIfNewUser().observe(this){
            if (it) {
                val intent = Intent(this, BoardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
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
                            Log.d("LoginUserActivity", "Token: ${data.loginResult.token}")
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