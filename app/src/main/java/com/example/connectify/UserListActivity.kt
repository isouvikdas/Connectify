package com.example.connectify

import com.google.firebase.firestore.Query.Direction
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connectify.data.Post
import com.example.connectify.data.PostDao
import com.example.connectify.databinding.ActivityUserListBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class UserListActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var binding: ActivityUserListBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil
            .setContentView(this, R.layout.activity_user_list)

        toolbar = findViewById(R.id.toolBar)

        setSupportActionBar(toolbar)
        postDao = PostDao()


        toggle = ActionBarDrawerToggle(this,
            binding.drawerLayout,
            toolbar,
            R.string.open, R.string.close,)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        toggle.isDrawerIndicatorEnabled = false


        toolbar.setNavigationOnClickListener {
            if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        binding.navigationView.visibility = View.GONE

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val alpha = 1 - slideOffset
                binding.drawerLayout.alpha = alpha
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                Toast.makeText(this@UserListActivity, "Drawer opened", Toast.LENGTH_SHORT).show()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                binding.drawerLayout.alpha = 1.0f
            }
        })

        binding.addButton.setOnClickListener{
            val intent = Intent(this@UserListActivity,
                PostActivity::class.java)
            startActivity(intent)
            finish()
        }

        setUpRecyclerView()


    }

    private fun setUpRecyclerView(){
        val postCollections = postDao.postCollection
        val query = postCollections.orderBy("createdAt", Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions
            .Builder<Post>()
            .setQuery(query,
                Post::class.java)
            .build()

        adapter = PostAdapter(recyclerViewOptions, this)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }
}