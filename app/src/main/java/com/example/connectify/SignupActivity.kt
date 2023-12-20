package com.example.connectify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.connectify.data.User
import com.example.connectify.data.UserDao
import com.example.connectify.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.Exception


class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var user : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_signup)

        db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        binding.signUpPageText.setOnClickListener{
            val intent = Intent(this@SignupActivity,
                LoginActivity::class.java)
            startActivity(intent)
        }

            binding.signUpButton.setOnClickListener {
                    createUser()
            }
    }

    private fun createUser(){
        val email = binding.emailSignUpEditText.text.toString()
        val password = binding.passwordSignUpEditText.text.toString()
        val rePassword = binding.rePasswordEditText.text.toString()
        val name = binding.nameSignUpEditText.text.toString()

        if (password == rePassword){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password)
                    val user = auth.currentUser
                    updateUI(user)
                    saveUserToFirestore(user!!.uid, name, email, password)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            baseContext,
                            "Oops! something went wrong. Please try again after sometime",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        else {
            binding.apply {
                rePasswordEditText.text.clear()
                Toast.makeText(baseContext,
                    "Confirm the password again",
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(firebaseUser: FirebaseUser?){
        if (firebaseUser != null) {
            val user = User(firebaseUser.uid, firebaseUser.email.toString(), firebaseUser.displayName.toString())
            val useDao = UserDao()
            useDao.addUser(user)
            startActivity(Intent(
                this@SignupActivity,
                UserListActivity::class.java))
            finish()
        }
    }


    private suspend fun saveUserToFirestore(uid: String, userName: String, email: String, password: String) {

            val user = hashMapOf(
                "name" to userName,
                "email" to email,
                "password" to password,
                "uid" to uid,
            )
        try {
            db.collection("users")
                .document(uid)
                .set(user)
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
}