package com.example.lobiupaieskossistema

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lobiupaieskossistema.caches.CacheAddActivity
import com.example.lobiupaieskossistema.caches.ManageCacheActivity
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.data.UserCacheData
import com.example.lobiupaieskossistema.data.UserData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.utils.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sessionManager: SessionManager
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        submitButton = findViewById(R.id.submitTreasureButton)
        submitButton.setOnClickListener {
            val cache = it.tag as? Cache
            if (cache != null) {
                AlertDialog.Builder(this)
                    .setTitle("Treasure Found!")
                    .setMessage("You have successfully found the treasure: ${cache.name}")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val profileButton: Button = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
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
    }

    private fun handleTabSelection(position: Int) {
        when (position) {
            0 -> {
                val intent = Intent(this, ManageCacheActivity::class.java)
                startActivity(intent)
            }

            1 -> {
                val intent = Intent(this, CacheAddActivity::class.java)
                startActivity(intent)
            }
            2 -> {
                val intent = Intent(this, AllGroupsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true

            // Start continuous location updates
            val locationRequest = LocationRequest.create().apply {
                interval = 5000 // Check every 5 seconds
                fastestInterval = 2000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val currentLocation = locationResult.lastLocation
                    if (currentLocation != null) {
                        updateCurrentLocation(currentLocation)
                    }
                }
            }, Looper.getMainLooper())
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        plotUserCaches()
    }

    private fun plotUserCaches() {
        val userId = sessionManager.getUserId()
        val userCaches = UserCacheData.getAll().filter { it.userId == userId && it.available == 1 }
        for (userCache in userCaches) {
            val cache = CacheData.get(userCache.cacheId)
            cache?.let {
                val position = LatLng(it.xCoordinate, it.yCoordinate)

                // Add marker for the cache
                val marker = map.addMarker(MarkerOptions().position(position).title(it.name))
                marker?.tag = it // Attach Cache object to marker

                // Add translucent circle representing the zoneRadius
                map.addCircle(
                    CircleOptions()
                        .center(position)
                        .radius(it.zoneRadius.toDouble()) // Radius in meters
                        .strokeColor(0x550000FF) // Semi-transparent blue for stroke
                        .fillColor(0x220000FF)   // Lighter semi-transparent blue for fill
                        .strokeWidth(2f)        // Stroke width in pixels
                )
            }
        }

        map.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }
    }

    private fun updateCurrentLocation(location: Location) {
        val userCaches = UserCacheData.getAll().filter { it.userId == sessionManager.getUserId() && it.available == 1 }

        for (userCache in userCaches) {
            val cache = CacheData.get(userCache.cacheId)
            cache?.let {
                if (isWithinRadius(location, it)) {
                    showSubmitButton(it)
                    return
                }
            }
        }

        hideSubmitButton() // Hide button if not within any radius
    }

    private fun isWithinRadius(userLocation: Location, cache: Cache): Boolean {
        val cacheLocation = Location("").apply {
            latitude = cache.xCoordinate
            longitude = cache.yCoordinate
        }
        return userLocation.distanceTo(cacheLocation) <= cache.zoneRadius
    }

    private fun showSubmitButton(cache: Cache) {
        submitButton.visibility = View.VISIBLE
        submitButton.text = "Submit Found Treasure for ${cache.name}" // Optional: personalize button text
        submitButton.tag = cache
    }

    private fun hideSubmitButton() {
        submitButton.visibility = View.GONE
    }

    private fun setDefaultLocation() {
        val defaultLocation = LatLng(54.6872, 25.2797) // Example: Vilnius coordinates
        map.addMarker(MarkerOptions().position(defaultLocation).title("Default Location"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
    }

    private fun getThemeName(themeId: Int): String {
        return when (themeId) {
            1 -> "Nature"
            2 -> "History"
            3 -> "Adventure"
            else -> "Unknown Theme"
        }
    }

    private fun getCreatorName(creatorId: Int): String {
        val user = UserData.get(creatorId) // Fetch the user by ID
        return user?.username ?: "Unknown Creator"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    map.isMyLocationEnabled = true
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val currentLocation = LatLng(location.latitude, location.longitude)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                        } else {
                            setDefaultLocation()
                        }
                    }
                }
            } else {
                setDefaultLocation()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
