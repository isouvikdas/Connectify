package com.example.connectify.data

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDao {

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")
    val currentTime = System.currentTimeMillis()
    private val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage("gs://connectify-a8959.appspot.com")
    var storageRef = storage.reference

    fun addUser(user: User?){
        user?.let {
            CoroutineScope(Dispatchers.IO).launch {
                userCollection.apply {
                    document(user.uid).set(it)
                }
            }
        }
    }

    fun addProfilePicture(imageUri: String) {
        val currentUser = auth.currentUser!!.uid

        val imageFileName = "Image_$currentTime"
        val folderPath = "profilePictures"
        val folderRef = storageRef.child(folderPath)

        val imagesRef = folderRef.child(imageFileName)
        val uploadTask = imagesRef.putFile(Uri.parse(imageUri))

        uploadTask.continueWithTask { task->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val downloadUri = task.result

                CoroutineScope(Dispatchers.IO).launch {
//                    val userDao = UserDao()
//                    val user = userDao.getUserId(currentUser).await().toObject(User::class.java)!!
//                    val post = Post(description, downloadUri.toString(), user, currentTime)
//                    postCollection.add(post).await()
                }


            } else {
                // Handle failures
                // ...
            }
        }

    }

    fun getUserId(uid: String): Task<DocumentSnapshot>{
        return userCollection.document(uid).get()
    }
}