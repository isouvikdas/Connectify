package com.example.connectify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.connectify.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_login)

        binding.loginPageText.setOnClickListener{
                val intent = Intent(this@LoginActivity,
                    SignupActivity::class.java)
                startActivity(intent)

        }
        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.emailLoginEditText.text.toString()
            val password = binding.passwordLoginEditText.text.toString()
            loginUser(email,password)

        }
    }

    private fun loginUser(email: String, password:String){
         CoroutineScope(Dispatchers.IO).launch {
             try {
                 auth.signInWithEmailAndPassword(email, password).await()

                 val user = auth.currentUser
                 updateUI(user)
             } catch (e: Exception){
                 withContext(Dispatchers.Main){
                     Toast.makeText(
                         baseContext,
                         "Incorrect Email or Password!",
                         Toast.LENGTH_LONG
                     ).show()
                 }
             }
         }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            val intent = Intent(this@LoginActivity,
                UserListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
        }
    }
}