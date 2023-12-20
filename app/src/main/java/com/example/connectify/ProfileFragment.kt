package com.example.connectify

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.connectify.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ProfileFragment : Fragment() {
    private lateinit var docRef: DocumentReference
    private var profileListener: ListenerRegistration? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_profile, container,
            false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        currentUserId = auth.currentUser!!.uid
        docRef = db.collection("users").document(currentUserId)
        getFireStoreData()

        startListening()

        binding.editProfileButton.setOnClickListener {
            moveToAnotherActivity(EditProfileActivity())
        }

        val signOutAlert = AlertDialog.Builder(this.context)
            .setTitle("Signing Out...")
            .setMessage("Are you sure want to sign out?")
            .setIcon(R.drawable.logout)
            .setPositiveButton("Yes") { _,_ ->
                signOut()
            }
            .setNegativeButton("No") { _,_ ->

            }
            .create()


        binding.signOutButton.setOnClickListener {
            signOutAlert.show()
        }

        return binding.root
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

            binding.userName.text = userName
            binding.userPronouns.text = userPronouns
            binding.userBio.text = userBio
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
                    binding.userName.setText(userName)
                    binding.userPronouns.setText(userPronouns)
                    binding.userBio.setText(userBio)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }



    private fun signOut() {
        auth.signOut()
        moveToAnotherActivity(LoginActivity())
    }

    private fun moveToAnotherActivity(activity: Activity) {
        val intent = Intent(this@ProfileFragment.requireContext(),
            activity::class.java)

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