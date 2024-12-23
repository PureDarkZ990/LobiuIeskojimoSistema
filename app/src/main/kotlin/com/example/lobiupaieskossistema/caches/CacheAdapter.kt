package com.example.lobiupaieskossistema.caches

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.CountDownTimer
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
import com.example.lobiupaieskossistema.data.ThemeData
import com.example.lobiupaieskossistema.data.UserCacheData
import java.io.File

class CacheAdapter(
    private val cacheList: MutableList<Cache>,
    private val context: Context,
    private val userRoleId: Int,
    private val userId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_ITEM = 1

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
        val themeTiming: TextView = itemView.findViewById(R.id.themeTiming)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emptyMessage: TextView = itemView.findViewById(R.id.emptyMessage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (cacheList.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.empty_view, parent, false)
            EmptyViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.cache_item, parent, false)
            CacheViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CacheViewHolder) {
            val cache = cacheList[position]
            holder.cacheName.text = cache.name
            holder.cacheDescription.text = cache.description
            holder.cachePublic.isChecked = cache.shown == 0
            holder.ratedComplexity.text = cache.difficulty?.toString() ?: "N/A"
            holder.userRating.text = cache.rating?.toString() ?: "N/A"
            holder.approved.text = if (cache.approved == 1) "Approved" else "Not Approved"

            val imagePath = File(context.filesDir, "cache-images/${cache.id}.png")
            if (imagePath.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath.absolutePath)
                holder.cacheImageView.setImageBitmap(bitmap)
            } else {
                holder.cacheImageView.setImageResource(R.drawable.default_image)
            }
            holder.cachePublic.setOnClickListener {
                cache.shown = if (cache.shown == 1) 0 else 1
                CacheData.update(cache)
                notifyItemChanged(position)
            }
            if (userRoleId == 1) {
                holder.approved.setOnClickListener {
                    cache.approved = if (cache.approved == 1) 0 else 1
                    CacheData.update(cache)
                    notifyItemChanged(position)
                }
                holder.deleteCacheButton.setOnClickListener {
                    AlertDialog.Builder(context)
                        .setTitle("Delete Cache")
                        .setMessage("Are you sure you want to delete this cache?")
                        .setPositiveButton("Yes") { _, _ ->
                            cacheList.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, cacheList.size)
                            CacheData.delete(cache.id)
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
                holder.editCacheButton.visibility = View.GONE
                holder.zoneRadiusButton.visibility = View.GONE
                holder.editThemeButton.visibility = View.GONE
            } else {
                holder.editCacheButton.setOnClickListener {
                    val intent = Intent(context, EditCacheActivity::class.java)
                    intent.putExtra("cacheId", cache.id)
                    context.startActivity(intent)
                }
                holder.zoneRadiusButton.setOnClickListener {
                    val intent = Intent(context, ChangeZoneActivity::class.java)
                    intent.putExtra("cacheId", cache.id)
                    context.startActivity(intent)
                }
                holder.deleteCacheButton.setOnClickListener {
                    AlertDialog.Builder(context)
                        .setTitle("Delete Cache")
                        .setMessage("Are you sure you want to delete this cache?")
                        .setPositiveButton("Yes") { _, _ ->
                            cacheList.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, cacheList.size)
                            CacheData.delete(cache.id)
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
                holder.editThemeButton.setOnClickListener {
                    val intent = Intent(context, ManageThemeActivity::class.java)
                    intent.putExtra("cacheId", cache.id)
                    context.startActivity(intent)
                }
                ThemeData.initialize(context)
                val theme = ThemeData.findThemeByCacheId(cache.id)
                if (theme != null) {
                    if (theme.endingTime > 0) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime < theme.endingTime) {
                            val remainingTime = theme.endingTime - currentTime
                            object : CountDownTimer(remainingTime, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    val seconds = millisUntilFinished / 1000
                                    val minutes = seconds / 60
                                    val hours = minutes / 60
                                    holder.themeTiming.text = "Time left: $hours:${minutes % 60}:${seconds % 60}"
                                }

                                override fun onFinish() {
                                    holder.themeTiming.text = "Cache expired"
                                    updateUserCacheAvailability(cache.id, 0)
                                }
                            }.start()
                        } else {
                            holder.themeTiming.text = "Cache expired"
                            updateUserCacheAvailability(cache.id, 0)
                        }
                    } else if (theme.time > 0) {
                        holder.themeTiming.text = "Time allowed in zone: ${theme.time / 60} minutes"
                    } else {
                        holder.themeTiming.visibility = View.GONE
                    }
                } else {
                    holder.themeTiming.visibility = View.GONE
                }
            }
        } else if (holder is EmptyViewHolder) {
            holder.emptyMessage.text = "Add More Caches"
        }
    }

    override fun getItemCount(): Int {
        return if (cacheList.isEmpty()) 1 else cacheList.size
    }

    private fun updateUserCacheAvailability(cacheId: Int, availability: Int) {
        UserCacheData.initialize(context)
        val userCaches = UserCacheData.getAll().filter { it.cacheId == cacheId && it.userId != userId }
        userCaches.forEach { userCache ->
            userCache.available = availability
            UserCacheData.add(userCache)
        }
    }
}