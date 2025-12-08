package com.example.groupproject.controller

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groupproject.R
import com.example.groupproject.model.Repository

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val recycler = findViewById<RecyclerView>(R.id.leaderboardRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        val repo = Repository.getInstance(this)


        val currentUsername: String? = repo.user?.username ?: repo.local.getLoggedInUser()

        repo.getAllUsers { userList ->

            val sortedList = userList.sortedByDescending { it.longestRun }


            recycler.adapter = LeaderboardAdapter(sortedList, currentUsername)
        }

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }
}
