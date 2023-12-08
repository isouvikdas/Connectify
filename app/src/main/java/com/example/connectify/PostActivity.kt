package com.example.connectify

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.connectify.data.PostDao
import com.example.connectify.databinding.ActivityPostBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPostBinding
    private lateinit var postDao : PostDao
    private  var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil
            .setContentView(this, R.layout.activity_post)


        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.storageButton.setOnClickListener {
            pickUpImage()
        }

        binding.postButton.setOnClickListener {
            handlePost()
        }

        setPostButtonColor(selectedImageUri)


    }

    private fun startCamera() {
        ImagePicker.with(this)
            .cameraOnly()
            .crop()
            .start()
    }

    private fun pickUpImage(){
        ImagePicker.with(this)
            .galleryOnly()
            .crop()
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.postImage.setImageURI(selectedImageUri)
            setPostButtonColor(selectedImageUri)
            Log.i("tagy", "it worked")
        }
        else {
            Log.i("tagy", "it didn't work")
        }
    }

    private suspend fun doPost(): Boolean = suspendCoroutine { continuation ->
        postDao = PostDao()
        CoroutineScope(Dispatchers.IO).launch {
            val imageURI = selectedImageUri.toString().trim()
            val description = binding.descriptionEditText.text.toString().trim()


            if(imageURI.isNotEmpty() && imageURI != "null") {
                postDao.addPost(imageURI, description)
                Log.i("tagy", "post worked")

                withContext(Dispatchers.Main) {
                    continuation.resume(true)
                }
            }
            else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        baseContext,
                        "Please select an image",
                        Toast.LENGTH_LONG
                    ).show()
                    continuation.resume(false)
                }
                Log.i("tagy", " image empty")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setPostButtonColor(selectedImageUri)
    }

    private fun setPostButtonColor(imageURI: Uri?) {
        if (imageURI != null) {
            binding.postButton.background = ContextCompat.getDrawable(this, R.drawable.postbutton_background)
            binding.postButton.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
        else {
            binding.postButton.background = ContextCompat.getDrawable(this, R.drawable.unpostbutton_background)
            binding.postButton.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    private fun moveToUserListActivity() {
        val intent = Intent(
            this@PostActivity,
            UserListActivity::class.java
        )
        startActivity(intent)
    }

    private fun handlePost() {
        CoroutineScope(Dispatchers.Main).launch {
            val postSuccessful = doPost()
            if (postSuccessful) {
                moveToUserListActivity()
            }
        }
    }
}