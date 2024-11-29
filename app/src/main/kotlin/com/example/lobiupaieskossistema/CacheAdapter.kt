package com.example.lobiupaieskossistema.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.ChangeZoneActivity
import com.example.lobiupaieskossistema.EditCacheActivity
import com.example.lobiupaieskossistema.ManageThemeActivity
import com.example.lobiupaieskossistema.CacheData

class CacheAdapter(private val cacheList: MutableList<Cache>, private val context: Context) : RecyclerView.Adapter<CacheAdapter.CacheViewHolder>() {

    class CacheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cacheName: TextView = itemView.findViewById(R.id.cacheName)
        val cacheDescription: TextView = itemView.findViewById(R.id.cacheDescription)
        val cachePublic: CheckBox = itemView.findViewById(R.id.cachePublic)
        val ratedComplexity: TextView = itemView.findViewById(R.id.ratedComplexity)
        val userRating: TextView = itemView.findViewById(R.id.userRating)
        val approved: TextView = itemView.findViewById(R.id.approved)
        val zoneRadiusButton: Button = itemView.findViewById(R.id.zoneRadiusButton)
        val editCacheButton: Button = itemView.findViewById(R.id.editCacheButton)
        val deleteCacheButton: Button = itemView.findViewById(R.id.deleteCacheButton)
        val editThemeButton: Button = itemView.findViewById(R.id.editThemeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CacheViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cache_item, parent, false)
        return CacheViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CacheViewHolder, position: Int) {
        val currentItem = cacheList[position]
        holder.cacheName.text = currentItem.name
        holder.cacheDescription.text = currentItem.description
        holder.cachePublic.isChecked = currentItem.isPublic
        holder.ratedComplexity.text = currentItem.complexity
        holder.userRating.text = currentItem.rating
        holder.approved.text = if (currentItem.isApproved) "Approved" else "Not Approved"
        holder.approved.setTextColor(
            if (currentItem.isApproved) holder.itemView.context.getColor(android.R.color.holo_green_dark)
            else holder.itemView.context.getColor(android.R.color.holo_red_dark)
        )

        // Set click listeners for buttons
        holder.zoneRadiusButton.setOnClickListener {
            val intent = Intent(context, ChangeZoneActivity::class.java)
            intent.putExtra("cacheId", currentItem.id)
            context.startActivity(intent)
        }

        holder.editCacheButton.setOnClickListener {
            val intent = Intent(context, EditCacheActivity::class.java)
            intent.putExtra("cacheId", currentItem.id)
            context.startActivity(intent)
        }

        holder.deleteCacheButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Cache")
                .setMessage("Are you sure you want to delete this cache?")
                .setPositiveButton("Yes") { _, _ ->
                    CacheData.deleteCache(currentItem.id)
                    cacheList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cacheList.size)
                }
                .setNegativeButton("No", null)
                .show()
        }

        holder.editThemeButton.setOnClickListener {
            val intent = Intent(context, ManageThemeActivity::class.java)
            intent.putExtra("cacheId", currentItem.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = cacheList.size
}