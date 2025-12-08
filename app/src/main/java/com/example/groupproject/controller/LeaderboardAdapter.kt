package com.example.groupproject.controller

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupproject.R
import com.example.groupproject.model.Repository
import com.example.groupproject.model.User

class LeaderboardAdapter(
    private val users: List<User>,
    private val currentUsername: String?
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.itemUsername)
        val distance: TextView = view.findViewById(R.id.itemDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]


        holder.username.text = user.username


        val distanceUnit =
            Repository.getInstance(holder.itemView.context).local.getDistanceUnit()
        holder.distance.text = String.format("%.2f %s", user.longestRun, distanceUnit)


        if (user.username == currentUsername) {
            holder.itemView.setBackgroundColor(Color.parseColor("#D0F0C0"))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}
