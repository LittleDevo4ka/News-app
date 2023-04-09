package com.example.newsapp.view

import android.content.Context
import android.content.DialogInterface.OnClickListener
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
import com.google.android.material.card.MaterialCardView

class NewsRecyclerItem(private val newsList: List<HomeNews>, private val context: Context,
onClickListener: onItemClickListener) :
    RecyclerView.Adapter<NewsRecyclerItem.MyViewHolder>() {

    private var mainListener: onItemClickListener = onClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    class MyViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.news_card_title)
        val cardSecondaryText: TextView = itemView.findViewById(R.id.news_card_secondary_text)
        val cardImage: ImageView = itemView.findViewById(R.id.news_card_image)

        init {
            itemView.findViewById<MaterialCardView>(R.id.news_card).setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.news_card, parent, false)
        return MyViewHolder(itemView, mainListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardTitle.text = newsList[position].title
        holder.cardSecondaryText.text = newsList[position].author

        if (newsList[position].urlToImage == null) {
            holder.cardImage.visibility = View.GONE
        } else {
            Glide.with(context).load(newsList[position].urlToImage)
                .placeholder(R.drawable.placeholder)
                .into(holder.cardImage)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}