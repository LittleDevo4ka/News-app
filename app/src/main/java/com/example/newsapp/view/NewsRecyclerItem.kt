package com.example.newsapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.model.retrofit.news.Article
import com.example.newsapp.model.room.HomeNews

class NewsRecyclerItem(private val newsList: List<HomeNews>, private val context: Context) :
    RecyclerView.Adapter<NewsRecyclerItem.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.news_card_title)
        val cardSecondaryText: TextView = itemView.findViewById(R.id.news_card_secondary_text)
        val cardImage: ImageView = itemView.findViewById(R.id.news_card_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.news_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardTitle.text = newsList[position].title
        holder.cardSecondaryText.text = newsList[position].author

        if (newsList[position].urlToImage == null) {
            holder.cardImage.visibility = View.GONE
        } else {
            Glide.with(context).load(newsList[position].urlToImage)
                .placeholder(R.drawable.ic_home)
                .into(holder.cardImage)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}