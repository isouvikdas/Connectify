package com.example.connectify

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.Target
import com.example.connectify.data.Post
import com.example.connectify.databinding.UseriTemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth

class PostAdapter( options: FirestoreRecyclerOptions<Post>, val listener: IPostAdapter) : FirestoreRecyclerAdapter<
        Post,
        PostAdapter.ViewHolder>
    (options) {
      private var utils = Utils()



    class ViewHolder(binding: UseriTemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.postImageView
        val description = binding.description
        val dateText = binding.dateText
        val likeCount = binding.likeCount
        val name = binding.userName
        val likeButton = binding.likeButton
        val shareButton = binding.shareButton
        val progressBar = binding.progressBar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : UseriTemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.useri_tem,
            parent,
            false
        )


        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Post) {
        val postId = snapshots.getSnapshot(position).id

        holder.apply {
            progressBar.visibility = View.VISIBLE
            GlideApp.with(imageView.context).load(model.imageURI).override(Target.SIZE_ORIGINAL).into(imageView)
            likeCount.text = model.likedBy.size.toString()
            name.text = model.createdBy.name
            dateText.text = utils.getTimeAgo(model.createdAt)
            description.text = model.description

            likeButton.setOnClickListener {
                listener.onLikeClicked(postId)
            }
            val auth = FirebaseAuth.getInstance()
            val currentUserID = auth.currentUser!!.uid
            val isLiked = model.likedBy.contains(currentUserID)
            if (isLiked) {
                likeButton.setBackgroundResource(R.drawable.liked)
            }
            else {
                likeButton.setBackgroundResource(R.drawable.unliked)
            }
            likeCount.text = model.likedBy.size.toString()

            shareButton.setOnClickListener {
                val imageURI = getItem(position).imageURI
                shareImage(itemView.context, imageURI)
            }

            if (!imageView.isInvisible)
            {
                progressBar.visibility = View.GONE
            }


        }
    }

    private fun shareImage(context: Context, imageURI: String ) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"

        val uri = Uri.parse(imageURI)

        intent.putExtra(Intent.EXTRA_STREAM, uri)

        context.startActivity(Intent.createChooser(intent, "Share this image using these..."))
    }
}