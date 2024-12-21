package com.example.lobiupaieskossistema.caches

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.data.CacheData
import java.io.File

class CacheAdapter(private val cacheList: MutableList<Cache>, private val context: Context) : RecyclerView.Adapter<CacheAdapter.CacheViewHolder>() {

    class CacheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cacheImageView: ImageView = itemView.findViewById(R.id.cacheImageView)
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
        val cache = cacheList[position]
        holder.cacheName.text = cache.name
        holder.cacheDescription.text = cache.description
        holder.cachePublic.isChecked = cache.private == 0
        holder.ratedComplexity.text = cache.difficulty?.toString() ?: "N/A"
        holder.userRating.text = cache.rating?.toString() ?: "N/A"
        holder.approved.text = if (cache.approved == 1) "Approved" else "Not Approved"

        // Load and display the cache image
        val imagePath = File(context.filesDir, "cache-images/${cache.id}.png")
        if (imagePath.exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath.absolutePath)
            holder.cacheImageView.setImageBitmap(bitmap)
        } else {
            holder.cacheImageView.setImageResource(R.drawable.default_image) // Set a default image if no image is found
        }

        holder.zoneRadiusButton.setOnClickListener {
            val intent = Intent(context, ChangeZoneActivity::class.java)
            intent.putExtra("cacheId", cache.id)
            context.startActivity(intent)
        }

        holder.editCacheButton.setOnClickListener {
            val intent = Intent(context, EditCacheActivity::class.java)
            intent.putExtra("cacheId", cache.id)
            context.startActivity(intent)
        }

        holder.deleteCacheButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Cache")
                .setMessage("Are you sure you want to delete this cache?")
                .setPositiveButton("Yes") { _, _ ->
                    // Remove the cache from the list and notify the adapter
                    cacheList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cacheList.size)
                    // Perform the actual deletion from the database if needed
                    CacheData.delete(cache.id)
                }
                .setNegativeButton("No", null)
                .show()
        }

        holder.editThemeButton.setOnClickListener {
            val intent = Intent(context, ManageThemeActivity::class.java)
            intent.putExtra("themeId", cache.themeId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return cacheList.size
    }
}