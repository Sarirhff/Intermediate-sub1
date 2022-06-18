package com.example.storyapp.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.login.LoginUserActivity
import com.example.storyapp.login.ProfileActivity
import com.example.storyapp.model.Result
import com.example.storyapp.model.UserPreference
import com.example.storyapp.ui.CameraActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory.getInstance(
            this,
            UserPreference.getInstance(dataStore)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        checkIfSessionValid()
    }

    private fun checkIfSessionValid() {
        mainViewModel.checkIfTokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                setupAction("Bearer $it")
            }
        }
    }

    private fun setupAction(token: String) {
        supportActionBar?.title = getString(R.string.app_name)
        mainViewModel.getStories(token).observe(this) {
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
                    if (data.isEmpty()) {
                        binding.tvNoStoryFound.visibility = View.VISIBLE
                    } else {
                        binding.tvNoStoryFound.visibility = View.INVISIBLE
                        binding.rvStory.apply {
                            adapter = StoryAdapter(this@MainActivity, data)
                            layoutManager = LinearLayoutManager(this@MainActivity)
                        }
                    }
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupButtons() {
        binding.btnAddStory.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_cam), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}