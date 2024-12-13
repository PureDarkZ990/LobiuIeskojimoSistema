package com.example.lobiupaieskossistema

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.TextView
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lobiupaieskossistema.caches.CacheAddActivity
import com.example.lobiupaieskossistema.caches.ManageCacheActivity
import com.example.lobiupaieskossistema.data.CacheData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.main_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        CacheData.initialize(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val profileButton: Button = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.manageCaches -> {
                    val intent = Intent(this, ManageCacheActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.addCache -> {
                    val intent = Intent(this, CacheAddActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.groupButton > {
                    val intent = Intent(this, GroupsActivity::class.java)
                    tartActivity(intent)
                    true
                }
                else -> false
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