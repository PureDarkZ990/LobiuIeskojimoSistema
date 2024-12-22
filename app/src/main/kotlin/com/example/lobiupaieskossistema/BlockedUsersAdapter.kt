package com.example.lobiupaieskossistema.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.models.User

class BlockedUsersAdapter(
    private val blockedUsers: List<User>,
    private val onUnblock: (User) -> Unit
) : RecyclerView.Adapter<BlockedUsersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.blockedUsername)
        val unblockButton: Button = view.findViewById(R.id.unblockButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blocked_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = blockedUsers[position]
        holder.username.text = user.username
        holder.unblockButton.setOnClickListener { onUnblock(user) }
    }

    override fun getItemCount() = blockedUsers.size
}
