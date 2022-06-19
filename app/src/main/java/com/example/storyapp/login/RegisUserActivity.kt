package com.example.storyapp.login


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisUserBinding
import com.example.storyapp.model.Result

class RegisUserActivity : AppCompatActivity() {

    private val binding: ActivityRegisUserBinding by lazy {
        ActivityRegisUserBinding.inflate(layoutInflater)
    }
    private val regisViewModel: RegisViewModel by viewModels {
        RegisViewModel.RegisViewModelFactory.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAction()
        hideSystemUI()

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

    private fun setupAction() {
        binding.buttonRegis.setOnClickListener {
            val name = binding.edtUsrnameRegis.text.toString()
            val email = binding.edtEmailRegis.text.toString()
            val password = binding.edtPasswordRegis.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val result = regisViewModel.registerUser(
                    name,
                    email,
                    password
                )
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
                            Toast.makeText(
                                this,
                                getString(R.string.sign_up_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, LoginUserActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                if (name.isEmpty()) binding.edtUsrnameRegis.error =
                    getString(R.string.empty_name)
                if (email.isEmpty()) binding.edtEmailRegis.error =
                    getString(R.string.empty_email)
                if (password.isEmpty()) binding.edtPasswordRegis.error =
                    getString(R.string.empty_pass)
            }
        }
        binding.toLogin.setOnClickListener {
            val intent = Intent(this, LoginUserActivity::class.java)
            startActivity(intent)
        }
    }

}