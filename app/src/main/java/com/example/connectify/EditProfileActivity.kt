package com.example.connectify

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.connectify.data.PostDao
import com.example.connectify.databinding.ActivityEditProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {
    private lateinit var docRef: DocumentReference
    private var profileListener : ListenerRegistration? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var postDao: PostDao
    private var selectedImageURi: Uri? = null
    private lateinit var editUserName : String
    private lateinit var editUserPronouns : String
    private lateinit var editUserBio : String
    private lateinit var downloadURL: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil
            .setContentView(this,
                R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        postDao = PostDao()

        currentUserId = auth.currentUser!!.uid
        docRef = db.collection("users").document(currentUserId)

        getFireStoreData()
        startListening()

        binding.editPicture.setOnClickListener {
            pickUpImage()
        }

        binding.saveButton.setOnClickListener {
             editUserName = binding.editName.text.toString()
             editUserPronouns = binding.editPronouns.text.toString()
             editUserBio = binding.editBio.text.toString()

            GlobalScope.launch(Dispatchers.IO) {

                updateUserDataToFirestore(
                    editUserBio,
                    editUserPronouns,
                    editUserName
                )

                uploadProfilePic(selectedImageURi!!)

            }
        }
    }

    private fun startListening() {
        profileListener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Current data: ${snapshot.data}")
                // Update UI based on the changes in the document
                updateUI(snapshot.data)
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    private fun updateUI(data: Map<String, Any>?) {
        if (data != null) {
            val userName = data["name"]?.toString()
            val userPronouns = data["userPronouns"]?.toString()
            val userBio = data["userBio"]?.toString()
            val userProfileURL = data["userProfileURL"]

            if (userProfileURL != null) {
                GlideApp.with(binding.profileImage.context).load(userProfileURL).into(binding.profileImage)
            } else {
                // Set a default image if the profile image is null
                binding.profileImage.setImageResource(R.drawable.empty_profile)
            }

            binding.editName.setText(userName)
            binding.editPronouns.setText(userPronouns)
            binding.editBio.setText(userBio)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageURi = data?.data
            binding.profileImage.setImageURI(selectedImageURi!!)
            Log.i("tagy", "it worked")
        }
        else {
            Log.i("tagy", "it didn't work")
        }
    }

    private suspend fun updateUserDataToFirestore(userBio: String,
                                                   userPronouns: String, userName: String){
        val updateUser = mapOf(
            "name" to userName,
            "userBio" to userBio,
            "userPronouns" to userPronouns
        )
        try {
            db.collection("users")
                .document(currentUserId)
                .update(updateUser)
                .await()
        } catch (e: Exception) {
            Log.i("tagy", "Failed to save user data to firestore")
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    baseContext,
                    "Failed to save user data to Firestore",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getFireStoreData() {
        docRef.get()
            .addOnSuccessListener {
                if (it != null) {
                    val userName = it.data?.get("name")?.toString()
                    val userPronouns = it.data?.get("userPronouns")?.toString()
                    val userBio = it.data?.get("userBio")?.toString()
                    val userProfileURL = it.data?.get("userProfileURL")

                    if (userProfileURL != null) {
                        GlideApp.with(binding.profileImage.context).load(userProfileURL).into(binding.profileImage)

                    } else {
                        binding.profileImage.setImageResource(R.drawable.empty_profile)
                    }
                    binding.editName.setText(userName)
                    binding.editPronouns.setText(userPronouns)
                    binding.editBio.setText(userBio)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun uploadProfilePic(imageUri: Uri?) {
        if (imageUri != null) {
            val currentTime = postDao.currentTime
            val storageRef = postDao.storageRef
            val imageFileName = "Image_$currentTime.jpg"
            val folderPath = "profilePictures"
            val folderRef = storageRef.child(folderPath)
            val imageRef = folderRef.child(imageFileName)
            val uploadTask = imageRef.putFile(imageUri)

            uploadTask.continueWithTask { task->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                        Log.i("$uploadTask", "$uploadTask didn't work")
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task->
                if (task.isSuccessful) {
                    val downloadURL = task.result

                    val updateProfilePic = mapOf(
                        "userProfileURL" to downloadURL.toString()
                    )
                    try {
                        db.collection("users")
                            .document(currentUserId)
                            .update(updateProfilePic)
                            .addOnCompleteListener {
                                // Update user posts (including username) irrespective of whether there is a new image or not
                                postDao.updateUserPosts(currentUserId, editUserName, editUserBio, editUserPronouns, downloadURL?.toString() ?: "")
                                moveToProfileFragment()
                            }
                            .addOnFailureListener {
                                Log.i("tagy", "failed to save user data to firestore:")
                            }
                    } catch (e: Exception) {
                        Log.i("tagy", "Failed to save user data to firestore")
                    }
                }
            }
        }
        else {
            postDao.updateUserPosts(currentUserId, editUserName, editUserBio, editUserPronouns, downloadURL.toString())
            moveToProfileFragment()
            Log.i("tagy", "No image selected for upload")
        }


    }

    private fun pickUpImage() {
        ImagePicker.with(this)
            .crop()
            .maxResultSize(1080, 1024)
            .start()
    }



    private fun moveToProfileFragment() {
        val intent = Intent(
            this,
            UserListActivity::class.java
        )
        intent.putExtra("showProfileFragment", true)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        stopListening()
    }

    private fun stopListening() {
        profileListener?.remove()
    }

}