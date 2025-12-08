package com.example.groupproject.controller

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R
import com.example.groupproject.model.Repository

class MapActivity : AppCompatActivity() {

    private lateinit var repo: Repository
    private var distanceGoal = 1.0
    private var currentDistance = 0.0
    private lateinit var distanceUnit: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        repo = Repository.getInstance(this)
        distanceUnit = repo.local.getDistanceUnit()

        val tvGoalValue = findViewById<TextView>(R.id.tvGoalValue)
        val tvDistanceValue = findViewById<TextView>(R.id.tvDistanceValue)
        val seek = findViewById<SeekBar>(R.id.seekGoal)
        val progress = findViewById<ProgressBar>(R.id.progressRun)

        seek.max = if (distanceUnit == "miles") 100 else 160 // Max 10 miles or 16 km
        progress.max = seek.max

        updateGoalText(tvGoalValue, seek.progress)
        tvDistanceValue.text = "Distance: 0.00 $distanceUnit"

        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                distanceGoal = value.toDouble() / if (distanceUnit == "miles") 10.0 else 10.0
                updateGoalText(tvGoalValue, value)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        findViewById<Button>(R.id.startRun).setOnClickListener {
            currentDistance = 0.0
            tvDistanceValue.text = "Distance: 0.00 $distanceUnit"
        }

        findViewById<Button>(R.id.endRun).setOnClickListener {
            val username = repo.local.getLoggedInUser()
            if (username != null) {
                repo.updateLongestRun(username, currentDistance)
                val user = repo.user
                if (user != null && currentDistance > user.longestRun) {
                    user.longestRun = currentDistance
                }
                finish()
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateGoalText(textView: TextView, progress: Int) {
        val goal = progress.toDouble() / if (distanceUnit == "miles") 10.0 else 10.0
        textView.text = String.format("Goal: %.1f %s", goal, distanceUnit)
    }
}