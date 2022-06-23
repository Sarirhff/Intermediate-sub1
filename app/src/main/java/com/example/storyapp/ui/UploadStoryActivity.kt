package com.example.storyapp.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import com.example.storyapp.database.AppExecutors
import com.example.storyapp.databinding.ActivityUploadStoryBinding
import com.example.storyapp.login.LoginUserActivity
import com.example.storyapp.main.MainActivity
import com.example.storyapp.model.Result
import com.example.storyapp.model.UserPreference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
class UploadStoryActivity : AppCompatActivity() {
    private var file: File? = null
    private var isBack: Boolean = true
    private var reducingDone: Boolean = false

    private val binding: ActivityUploadStoryBinding by lazy {
        ActivityUploadStoryBinding.inflate(layoutInflater)
    }

    private val cameraViewModel: UploadStoryViewModel by viewModels {
        UploadStoryViewModel.UploadStoryViewModelFactory.getInstance(
            this,
            UserPreference.getInstance(dataStore)
        )
    }

    private val appExecutor: AppExecutors by lazy {
        AppExecutors()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Upload Story"
        supportActionBar?.setDisplayShowHomeEnabled(true)

        bindResult()
        setupAction()
    }
    fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }
    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun bindResult() {
        file = intent.getSerializableExtra(PHOTO_RESULT_EXTRA) as File
        isBack = intent.getBooleanExtra(IS_CAMERA_BACK_EXTRA, true)

        val result = rotateBitmap(BitmapFactory.decodeFile((file as File).path), isBack)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

        appExecutor.diskIO.execute {
            file = reduceFileImage(file as File)
            reducingDone = true
        }


        binding.imgUpdateStory.setImageBitmap(result)
    }

    private fun setupAction() {
        binding.btnUploadstory.setOnClickListener {
            cameraViewModel.checkIfTokenAvailable().observe(this) {
                if (reducingDone) {
                    if (it == "null") {
                        val intent = Intent(this, LoginUserActivity::class.java)
                        startActivity(intent)
                    } else {
                        uploadImage("Bearer $it")
                    }
                } else {
                    Toast.makeText(this, getString(R.string.wait_picture), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun uploadImage(token: String) {
        if (binding.edtDescStory.text.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.descrip_empty), Toast.LENGTH_SHORT).show()
        } else {
            if (file != null) {
                binding.loadingBar.visibility = View.VISIBLE
                val description = binding.edtDescStory.text.toString()
                val result = cameraViewModel.addNewStory(token, file as File, description)
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
                                getString(R.string.story_success),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val PHOTO_RESULT_EXTRA = "photo_result_extra"
        const val IS_CAMERA_BACK_EXTRA = "is_camera_back_extra"
    }
}