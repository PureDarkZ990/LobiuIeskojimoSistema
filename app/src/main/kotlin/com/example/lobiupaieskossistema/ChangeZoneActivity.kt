package com.example.lobiupaieskossistema

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.models.Cache
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng

class ChangeZoneActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var zoneRadiusSlider: SeekBar
    private lateinit var radiusValueText: TextView
    private lateinit var confirmZoneButton: Button
    private var cacheId: Int = -1
    private var cache: Cache? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_zone)

        cacheId = intent.getIntExtra("cacheId", -1)
        cache = CacheData.getCacheById(cacheId)

        zoneRadiusSlider = findViewById(R.id.zoneRadiusSlider)
        radiusValueText = findViewById(R.id.radiusValueText)
        confirmZoneButton = findViewById(R.id.confirmZoneButton)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        zoneRadiusSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radiusValueText.text = "Radius: $progress m"
                updateMapRadius(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        confirmZoneButton.setOnClickListener {
            cache?.let {
                it.zoneRadius = zoneRadiusSlider.progress
                CacheData.updateCache(it)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        cache?.let {
            val location = LatLng(it.latitude, it.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            updateMapRadius(it.zoneRadius)
        }
    }

    private fun updateMapRadius(radius: Int) {
        map.clear()
        cache?.let {
            val location = LatLng(it.latitude, it.longitude)
            map.addCircle(CircleOptions()
                .center(location)
                .radius(radius.toDouble())
                .strokeColor(0x220000FF)
                .fillColor(0x220000FF))
        }
    }
}