package com.example.lobiupaieskossistema

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.TextView
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lobiupaieskossistema.caches.CacheAddActivity
import com.example.lobiupaieskossistema.caches.ManageCacheActivity
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.data.GroupData
import com.example.lobiupaieskossistema.data.UserCacheData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.utils.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.main_map)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        CacheData.initialize(this)
        UserCacheData.initialize(this)
        GroupData.initialize(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val profileButton: Button = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
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
                val intent = Intent(this, GroupsActivity::class.java)
                startActivity(intent)
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
                } else {
                    setDefaultLocation()
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
        plotUserCaches()
    }
    private fun plotUserCaches() {
        val userId = sessionManager.getUserId()
        val userCaches = UserCacheData.getAll().filter { it.userId == userId && it.available == 1 }
        for (userCache in UserCacheData.getAll()) {
            println("UserCache: $userCache")
        }
        for (userCache in userCaches) {
            val cache = CacheData.get(userCache.cacheId)
            cache?.let {
                val position = LatLng(it.xCoordinate, it.yCoordinate)
                val marker = map.addMarker(MarkerOptions().position(position).title(it.name))
                marker?.tag = it
            }
        }

        map.setOnMarkerClickListener { marker ->
            val cache = marker.tag as? Cache
            cache?.let {
                showAlert(it)
            }
            true
        }
    }

    private fun showAlert(cache: Cache) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(cache.name)
        builder.setMessage(cache.description)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun setDefaultLocation() {
        val defaultLocation = LatLng(54.6872, 25.2797) // Example: Vilnius coordinates
        map.addMarker(MarkerOptions().position(defaultLocation).title("Default Location"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
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
                            map.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
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