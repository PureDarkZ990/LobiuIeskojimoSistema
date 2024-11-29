package com.example.lobiupaieskossistema

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.main_map)

        // Initialize the map fragment and set a callback for when it's ready
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Button to open the profile activity
        val profileButton: Button = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Handle BottomNavigationView item clicks
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.manageCaches -> {
                    var intent=Intent(this,ManageCacheActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.addCache -> {
                    val intent = Intent(this, CacheAddActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.manageGroups -> {
                    true
                }
                else -> false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Set a default location and zoom level
        val defaultLocation = LatLng(54.6872, 25.2797) // Coordinates for Vilnius, Lithuania
        map.addMarker(MarkerOptions().position(defaultLocation).title("Marker in Vilnius"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
    }
}