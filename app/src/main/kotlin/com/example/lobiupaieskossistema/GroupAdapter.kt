package com.example.lobiupaieskossistema

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.lobiupaieskossistema.models.Group

class GroupAdapter(private val context: Context, private val groups: List<Group>) : BaseAdapter() {

    override fun getCount(): Int {
        return groups.size
    }

    override fun getItem(position: Int): Any {
        return groups[position]
    }

    override fun getItemId(position: Int): Long {
        return groups[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val group = groups[position]
        val view: View

        if (convertView == null) {
            // Inflate a new view if convertView is null
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_group, parent, false)
        } else {
            view = convertView
        }

        // Bind data to the views
        val groupNameTextView: TextView = view.findViewById(R.id.groupNameTextView)
        val groupDescriptionTextView: TextView = view.findViewById(R.id.groupDescriptionTextView)

        groupNameTextView.text = group.name
        groupDescriptionTextView.text = group.description

        return view
    }
}