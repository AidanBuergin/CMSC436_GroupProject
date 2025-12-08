package com.example.groupproject.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R
import com.example.groupproject.model.Repository
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class HomeActivity : AppCompatActivity() {

    private lateinit var adView: AdView
    private lateinit var repo: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        repo = Repository.getInstance(this)
        val user = repo.user

        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Welcome text (optional)
        val welcomeTv = findViewById<TextView>(R.id.welcomeText)
        welcomeTv?.text = "Welcome, ${user.username}"

        // Buttons
        val btnLeaderboard = findViewById<Button>(R.id.btnLeaderboard)
        val btnMap = findViewById<Button>(R.id.btnMap)

        btnLeaderboard.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        btnMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // Distance unit preference
        val unitRadioGroup = findViewById<RadioGroup>(R.id.unitRadioGroup)
        val milesRadioButton = findViewById<RadioButton>(R.id.milesRadioButton)
        val kmRadioButton = findViewById<RadioButton>(R.id.kmRadioButton)

        if (repo.local.getDistanceUnit() == "miles") {
            milesRadioButton.isChecked = true
        } else {
            kmRadioButton.isChecked = true
        }

        unitRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.milesRadioButton -> repo.local.saveDistanceUnit("miles")
                R.id.kmRadioButton -> repo.local.saveDistanceUnit("km")
            }
        }

        // ---- Ad setup (Google test banner) ----
        adView = AdView(this)
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111" // Google test banner id
        adView.setAdSize(AdSize.BANNER)

        val request = AdRequest.Builder()
            .addKeyword("fitness")
            .addKeyword("running")
            .build()

        val adLayout = findViewById<LinearLayout>(R.id.ad_view)
        adLayout.addView(adView)
        adView.loadAd(request)
    }

    override fun onPause() {
        if (::adView.isInitialized) adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (::adView.isInitialized) adView.resume()
    }

    override fun onDestroy() {
        if (::adView.isInitialized) adView.destroy()
        super.onDestroy()
    }
}