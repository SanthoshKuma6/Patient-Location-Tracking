package com.med.medicalapplication.activity

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.med.medicalapplication.R
import com.med.medicalapplication.databinding.ActivityMapsBinding
import com.med.medicalapplication.mvvm.DataBase
import com.med.medicalapplication.mvvm.LocationTable
import com.med.medicalapplication.mvvm.PatientFactory
import com.med.medicalapplication.mvvm.PatientModelClass
import com.med.medicalapplication.mvvm.PatientRepository
import com.med.medicalapplication.mvvm.PatientViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private  lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: PatientViewModel
    private var lat = ""
    private var lan = ""
    private var address = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = PatientRepository(DataBase.getDatabase(this))
        val factory = PatientFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PatientViewModel::class.java]
        onLocationChanged()
//        list = mutableListOf(
//            LocationTable("11.68", "49.10"),
//            LocationTable("11.68", "49.13"),
//            LocationTable("11.01", "77.10"),
//            LocationTable("11.23", "78.10"),
//            LocationTable("11.57", "80.48"),
//            LocationTable("11.43", "77.40"),
//        )
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        lifecycleScope.launchWhenCreated {
            viewModel.getLocation().collect {
                mMap?.let { it1 -> getHistory(it1, it) }
            }
        }

    }

    private fun getHistory(googleMap: GoogleMap, place: List<LocationTable>) {
        for (i in place) {
            mMap = googleMap
            mMap!!.isIndoorEnabled = true

            // Add a marker in Sydney and move the camera
            try {
                val sydney = i.lat?.let { i.lan?.let { it1 -> LatLng(it.toDouble(), it1.toDouble()) } }
                sydney?.let { CameraUpdateFactory.newLatLng(it) }?.let { mMap!!.moveCamera(it) }
                val numMarkersInRainbow = 12
                for (j in 0 until numMarkersInRainbow) {
                    i.lat?.let {
                        i.lan?.let { it1 ->
                            LatLng(
                                it.toDouble(),
                                it1.toDouble()
                            )
                        }
                    }?.let {
                        MarkerOptions()
                            .position(
                                it
                            )
                            .title("Marker World")
                            .icon(BitmapDescriptorFactory.defaultMarker((j * 360 / numMarkersInRainbow).toFloat()))
                    }?.let {
                        mMap!!.addMarker(
                            it
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(lat.toDouble(), lan.toDouble())
        mMap!!.addMarker(MarkerOptions().position(sydney).title(address))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }

    private fun onLocationChanged() {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(lat.toDouble(), lan.toDouble(), 1) as List<Address>
            address = addresses[0].getAddressLine(0)
            Log.i("TAG", "onLocationChanged: $addresses")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}