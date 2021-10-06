package com.project22.myapplication.adapters

import android.R.attr
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.project22.myapplication.R
import com.project22.myapplication.model.UserProfile
import android.R.attr.label
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import android.R.attr.label
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build

import androidx.core.content.ContextCompat.getSystemService
import android.view.Gravity

import android.widget.Toast
import android.widget.Toast.LENGTH_LONG

import androidx.core.content.ContextCompat.getSystemService
import com.firebase.ui.auth.AuthUI.getApplicationContext


class UserViewHolder(private val userList: ArrayList<UserProfile>) :
    RecyclerView.Adapter<UserViewHolder.UserView>() {



    public class UserView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val singleUserNameHolder: TextView = itemView.findViewById(R.id.singleUserNameHolder)
        val singleUserImageHolder: ImageView  = itemView.findViewById(R.id.singleUserImageHolder)
        val singleUserIdHolder: TextView = itemView.findViewById(R.id.singleUserIdHolder)
        val cardOfUserView: MaterialCardView = itemView.findViewById(R.id.cardOfUserView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserView {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.single_user_item,parent,false)
        return UserView(itemView)
    }

    override fun onBindViewHolder(holder: UserView, position: Int) {

        val dest = userList[position]

        holder.singleUserNameHolder.text = dest.user.toString()
        holder.singleUserIdHolder.text = dest.userId.toString()


        Glide.with(holder.itemView.context)
            .load(dest.profileImageUrl)
            .placeholder(R.drawable.travel)
            .into(holder.singleUserImageHolder)

        holder.cardOfUserView.setOnClickListener {
            val clipboard = ContextCompat.getSystemService(holder.itemView.context,ClipboardManager::class.java)
            clipboard?.setPrimaryClip(ClipData.newPlainText("","Name: ${dest.user.toString()} User ID: ${dest.userId.toString()}"))
            Toast.makeText(holder.itemView.context,"Copied User Information",LENGTH_LONG).show()

        }

    }


    override fun getItemCount(): Int {
        return userList.size
    }
}