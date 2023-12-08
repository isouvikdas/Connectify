package com.example.connectify.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserDao {

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    fun addUser(user: User?){
        user?.let {
            CoroutineScope(Dispatchers.IO).launch {
                userCollection.apply {
                    document(user.uid).set(it)
                }
            }
        }
    }

    fun getUserId(uid: String): Task<DocumentSnapshot>{
        return userCollection.document(uid).get()
    }
}