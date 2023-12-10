package com.example.connectify

import com.google.firebase.firestore.Query.Direction
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connectify.data.Post
import com.example.connectify.data.PostDao
import com.example.connectify.databinding.ActivityUserListBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class UserListActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var binding: ActivityUserListBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil
            .setContentView(this, R.layout.activity_user_list)

        postDao = PostDao()

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