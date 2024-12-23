package com.example.lobiupaieskossistema.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.models.User

class FriendsAdapter(
    private val friends: List<User>,
    private val onActionClick: (User, String) -> Unit
) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.friendUsername)
        val removeButton: Button = view.findViewById(R.id.removeFriendButton)
        val blockButton: Button = view.findViewById(R.id.blockFriendButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        holder.username.text = friend.username
        holder.removeButton.setOnClickListener { onActionClick(friend, "remove") }
        holder.blockButton.setOnClickListener { onActionClick(friend, "block") }
    }

    override fun getItemCount() = friends.size
}
