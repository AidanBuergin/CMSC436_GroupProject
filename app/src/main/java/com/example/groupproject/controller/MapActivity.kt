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
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.location.Location
import androidx.activity.result.ActivityResultCallback
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var lastLocation: Location? = null
    private var isRunning: Boolean = false
    private lateinit var map: GoogleMap

    private lateinit var repo: Repository
    private var distanceGoal = 1.0
    private var currentDistance = 0.0
    private lateinit var distanceUnit: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        repo = Repository.getInstance(this)
        distanceUnit = repo.local.getDistanceUnit()

        val fragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragment.getMapAsync(this)

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
            lastLocation = null
            currentDistance = 0.0
            tvDistanceValue.text = "Distance: 0.00 $distanceUnit"
            isRunning = true
            progress.progress = 0

            checkLocationPermissionAndStart()
        }

        findViewById<Button>(R.id.endRun).setOnClickListener {
            isRunning = false
            locationClient.removeLocationUpdates(locationCallback)

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

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        val builder = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
        locationRequest = builder.build()

        val contract = ActivityResultContracts.RequestPermission()
        val callback = LocationPermissionResult()
        permissionLauncher = this.registerForActivityResult(contract, callback)
    }

    private fun updateGoalText(textView: TextView, progress: Int) {
        val goal = progress.toDouble() / if (distanceUnit == "miles") 10.0 else 10.0
        textView.text = String.format("Goal: %.1f %s", goal, distanceUnit)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (!isRunning) return

            val location = result.lastLocation ?: return
            if (lastLocation != null) {
                val deltaMeters = lastLocation!!.distanceTo(location)
                updateDistance(deltaMeters)
            }

            lastLocation = location

            if (::map.isInitialized) {
                val here = LatLng(location.latitude, location.longitude)
                val update = CameraUpdateFactory.newLatLngZoom(here, 17.0f)
                map.animateCamera(update)
            }
        }
    }

    inner class LocationPermissionResult : ActivityResultCallback<Boolean> {
        override fun onActivityResult(result: Boolean) {
            if (result) {
                startLocationUpdates()
            } else {
                Toast.makeText(
                    this@MapActivity,
                    "Location permission is required to track the run distance",
                    Toast.LENGTH_SHORT
                ).show()
                isRunning = false
            }
        }
    }

    private fun updateDistance(change: Float) {
        val delta: Double
        if (distanceUnit == "miles") {
            delta = change / 1609.34
        } else {
            delta = change / 1000.0
        }

        currentDistance += delta

        val tvDistanceValue = findViewById<TextView>(R.id.tvDistanceValue)
        val progressBar = findViewById<ProgressBar>(R.id.progressRun)

        tvDistanceValue.text = String.format("Distance: %.2f %s", currentDistance, distanceUnit)

        val goal = distanceGoal.coerceAtLeast(0.1)
        val fraction = (currentDistance / goal).coerceIn(0.0, 1.0)
        progressBar.progress = (fraction * progressBar.max).toInt()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private fun checkLocationPermissionAndStart() {
        val permissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionGranted == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }
}