package com.project22.myapplication.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project22.myapplication.R

class TextMessageSenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var text_view_message: TextView
    var text_view_time: TextView
    var text_view_user_name: TextView



    init {

        text_view_message = view.findViewById(R.id.textMessage)
        text_view_time = view.findViewById(R.id.textMessageTime)
        text_view_user_name = view.findViewById(R.id.textMessageUserName)



    }
}