package com.example.lobiupaieskossistema

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lobiupaieskossistema.caches.CacheAddActivity
import com.example.lobiupaieskossistema.caches.CacheInfoWindowAdapter
import com.example.lobiupaieskossistema.caches.ManageCacheActivity
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.data.UserCacheData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.UserCache
import com.example.lobiupaieskossistema.utils.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sessionManager: SessionManager
    private var lastKnownLocation: Location? = null
    private val markers = mutableMapOf<Int, Marker>()
    private val circles = mutableMapOf<Int, Circle>()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserCacheData.initialize(applicationContext)
        supportActionBar?.hide()
        setContentView(R.layout.main_map)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LogInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val profileButton = findViewById<Button>(R.id.profileButton)
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val filterButton = findViewById<Button>(R.id.filterButton)
        filterButton.setOnClickListener {
            showFilterDialog()
        }

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        if (sessionManager.isAdministrator()) {
            val addCacheTab = tabLayout.getTabAt(1)
            addCacheTab?.view?.visibility = View.GONE
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                handleTabSelection(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                handleTabSelection(tab.position)
            }
        })

        startMapRefresh()
    }

    private fun handleTabSelection(position: Int) {
        when (position) {
            0 -> startActivity(Intent(this, ManageCacheActivity::class.java))
            1 -> startActivity(Intent(this, CacheAddActivity::class.java))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setInfoWindowAdapter(CacheInfoWindowAdapter(this, ::isWithinRadius) { cache ->
            refreshMapData() // Refresh map after cache interaction
        })

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            enableMyLocation()
        }

        refreshMapData()

        map.setOnInfoWindowClickListener { marker ->
            val cache = marker.tag as? Cache
            if (cache != null) {
                showCacheDialog(cache)
            } else {
                Toast.makeText(this, "Error: Cache data not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    lastKnownLocation = it
                    val currentLocation = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                }
            }.addOnFailureListener { e ->
                Log.e("MainActivity", "Error getting location: ${e.message}")
            }
        }
    }

    private fun refreshMapData() {
        val userCaches = UserCacheData.getAll().filter { it.userId == sessionManager.getUserId() && it.available == 1 }
        val existingCacheIds = mutableSetOf<Int>()

        userCaches.forEach { userCache ->
            val cache = CacheData.get(userCache.cacheId)
            cache?.let {
                existingCacheIds.add(it.id)
                val position = LatLng(it.xCoordinate, it.yCoordinate)

                val marker = markers[it.id]
                if (marker == null) {
                    val newMarker = map.addMarker(MarkerOptions().position(position).title(it.name))
                    newMarker?.tag = it
                    markers[it.id] = newMarker!!
                }

                val circle = circles[it.id]
                if (circle == null) {
                    val newCircle = map.addCircle(
                        CircleOptions()
                            .center(position)
                            .radius(it.zoneRadius.toDouble())
                            .strokeColor(0x550000FF)
                            .fillColor(0x220000FF)
                            .strokeWidth(2f)
                    )
                    circles[it.id] = newCircle
                }
            }
        }

        val removedCacheIds = markers.keys - existingCacheIds
        removedCacheIds.forEach { id ->
            markers[id]?.remove()
            markers.remove(id)
            circles[id]?.remove()
            circles.remove(id)
        }
    }

    private fun isWithinRadius(cache: Cache): Boolean {
        if (lastKnownLocation == null) {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show()
            return false
        }

        val cacheLocation = Location("").apply {
            latitude = cache.xCoordinate
            longitude = cache.yCoordinate
        }

        val distance = lastKnownLocation!!.distanceTo(cacheLocation)
        return distance <= cache.zoneRadius
    }

    private fun showCacheDialog(cache: Cache) {
        AlertDialog.Builder(this)
            .setTitle(cache.name ?: "No Title")
            .setMessage("Description: ${cache.description ?: "No Description"}")
            .setPositiveButton("Submit Treasure") { _, _ ->
                if (isWithinRadius(cache)) {
                    handleCacheInteraction(cache)
                } else {
                    Toast.makeText(this, "You are not within the radius!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleCacheInteraction(cache: Cache) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password_prompt, null)
        val passwordInput = dialogView.findViewById<EditText>(R.id.passwordInput)

        AlertDialog.Builder(this)
            .setTitle("Enter Password for ${cache.name}")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val enteredPassword = passwordInput.text.toString()
                if (enteredPassword == cache.password) {
                    showRatingDialog(cache)
                } else {
                    Toast.makeText(this, "Incorrect password. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRatingDialog(cache: Cache) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rating_prompt, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        AlertDialog.Builder(this)
            .setTitle("Rate ${cache.name}")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val rating = ratingBar.rating.toDouble()
                submitRatingAndUpdateStatus(cache, rating)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun submitRatingAndUpdateStatus(cache: Cache, rating: Double) {
        val userCache = UserCache(
            userId = sessionManager.getUserId(),
            cacheId = cache.id,
            found = 1,
            rating = rating,
            available = 0
        )
        UserCacheData.update(userCache)

        refreshMapData()

        Toast.makeText(this, "Thank you for rating ${cache.name}!", Toast.LENGTH_SHORT).show()
    }

    private fun startMapRefresh() {
        val refreshHandler = android.os.Handler()
        val refreshRunnable = object : Runnable {
            override fun run() {
                refreshMapData()
                refreshHandler.postDelayed(this, 5000)
            }
        }
        refreshHandler.post(refreshRunnable)
    }

    private fun showFilterDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter_prompt, null)
        val difficultyInput = dialogView.findViewById<EditText>(R.id.filterDifficultyInput)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.filterRatingBar)
        val foundCheckbox = dialogView.findViewById<CheckBox>(R.id.filterFoundCheckbox)

        AlertDialog.Builder(this)
            .setTitle("Filter Caches")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                val difficulty = difficultyInput.text.toString().toDoubleOrNull()
                val rating = ratingBar.rating.toDouble()
                val showFound = foundCheckbox.isChecked

                applyFilters(difficulty, rating, showFound)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applyFilters(difficulty: Double?, rating: Double, showFound: Boolean) {
        val userCaches = UserCacheData.getAll().filter { it.userId == sessionManager.getUserId() }

        markers.values.forEach { it.isVisible = false }
        circles.values.forEach { it.isVisible = false }

        userCaches.forEach { userCache ->
            val cache = CacheData.get(userCache.cacheId)
            cache?.let {
                val matchesDifficulty = difficulty == null || it.difficulty == difficulty
                val matchesRating = it.rating ?: 0.0 >= rating
                val matchesFound = (userCache.found == 1) == showFound

                if (matchesDifficulty && matchesRating && matchesFound) {
                    markers[it.id]?.isVisible = true
                    circles[it.id]?.isVisible = true
                }
            }
        }
    }
}
