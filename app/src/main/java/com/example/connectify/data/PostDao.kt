package com.example.connectify.data

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class PostDao {
    private val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("posts")
    val currentTime = System.currentTimeMillis()
    private val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage("gs://connectify-a8959.appspot.com")
    var storageRef = storage.reference



    fun addPost(imageUri: String, description: String) {
        val currentUser = auth.currentUser!!.uid

        val imageFileName = "Image_$currentTime"
        val folderPath = "postImages"
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
                    val userDao = UserDao()
                    val user = userDao.getUserId(currentUser).await().toObject(User::class.java)!!
                    val post = Post(description, downloadUri.toString(), user, currentTime)
                    postCollection.add(post).await()
                }


            } else {
                // Handle failures
                // ...
            }
        }

    }

    fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }

    fun updateLikes(postId: String) {
        val currentUserID = auth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO) {

            try {
                val post = getPostById(postId).await().toObject(Post::class.java)
                if (post != null) {
                    val isLiked = post.likedBy.contains(currentUserID)
                    if (isLiked) {
                        post.likedBy.remove(currentUserID)
                    } else {
                        post.likedBy.add(currentUserID)
                    }
                    postCollection.document(postId).set(post)
                }

            } catch (e: Exception) {

            }
        }
    }

    // Add this function to your PostDao class
    fun updateUserPosts(uid: String, updatedUserName: String, updatedUserBio: String, updatedUserPronouns: String, userProfileURL: String) {

        val updateUserData = mapOf(
            "createdBy.name" to updatedUserName,
            "createdBy.userBio" to updatedUserBio,
            "createdBy.userPronouns" to updatedUserPronouns,
            "createdBy.userProfileURL" to userProfileURL
        )
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Get all posts created by the user
                val userPosts = postCollection.whereEqualTo("createdBy.uid", uid).get().await()
                // Update each post with the new user data
                userPosts.documents.forEach { document ->
                    val postId = document.id

                    postCollection.document(postId).update(updateUserData).await()
                    Log.i("PostDao", "Error updating user posts: $updateUserData")
                }
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("PostDao", "Error updating user posts: $e")
            }
        }
    }

}