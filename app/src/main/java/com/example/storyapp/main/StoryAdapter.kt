package com.example.storyapp.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.model.UserEntity
import com.example.storyapp.ui.DetailStoryActivity

class StoryAdapter (private val context: Context, private val list: List<UserEntity>) :
    RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapter.ViewHolder, position: Int) {
        val storyUser = list[position]
        holder.binding.tvStoryUsername.text = storyUser.name
        holder.binding.tvDescripStory.text = storyUser.description

        Glide.with(holder.itemView.context)
            .load(storyUser.photoUrl)
            .into(holder.binding.imgStory)

        holder.binding.cardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailStoryActivity::class.java)
            intent.putExtra(DetailStoryActivity.NAME_DETAIL_EXTRA, storyUser.name)
            intent.putExtra(DetailStoryActivity.DESCRIPTION_DETAIL_EXTRA, storyUser.description)
            intent.putExtra(DetailStoryActivity.IMAGE_URL_DETAIL_EXTRA, storyUser.photoUrl)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.binding.imgStory, "picture"),
                    Pair(holder.binding.tvDescripStory, "description")
                )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)
}