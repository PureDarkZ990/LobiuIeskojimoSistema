package com.example.lobiupaieskossistema.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.models.User

class PendingRequestsAdapter(
    private val requesters: List<User>,
    private val onActionClick: (User, String) -> Unit
) : RecyclerView.Adapter<PendingRequestsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.requesterUsername)
        val acceptButton: Button = view.findViewById(R.id.acceptRequestButton)
        val rejectButton: Button = view.findViewById(R.id.rejectRequestButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requester = requesters[position]
        holder.username.text = requester.username
        holder.acceptButton.setOnClickListener { onActionClick(requester, "accept") }
        holder.rejectButton.setOnClickListener { onActionClick(requester, "reject") }
    }

    override fun getItemCount() = requesters.size
}
