package com.example.connectify


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.connectify.databinding.ActivityUserListBinding

class UserListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil
            .setContentView(this, R.layout.activity_user_list)
    loadFragment(HomeFragment())

        binding.navigationView.setOnItemSelectedListener {
            it.isChecked = true
            when(it.itemId) {
                R.id.Home -> {
                    loadFragment(HomeFragment())
                }
                R.id.Profile -> {
                    loadFragment(ProfileFragment())
                }
            }
            true
        }

        binding.addButton.setOnClickListener{
            val intent = Intent(this@UserListActivity,
                PostActivity::class.java)
            startActivity(intent)
        }

        if (intent.getBooleanExtra("showProfileFragment", false)) {
            val fragment = ProfileFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment) // R.id.container is the ID of the container where you want to place the fragment
                .commit()
        }

    }

    private  fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}