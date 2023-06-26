package com.med.medicalapplication.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.med.medicalapplication.location.Constant.ACTION_START_FUSED_SERVICE
import com.med.medicalapplication.location.Constant.ACTION_STOP_FUSED_SERVICE


class FusedLocationService : LifecycleService() {


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    companion object {
        val latitudeFlow = MutableLiveData<Location>()
    }


    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient= FusedLocationProviderClient(applicationContext)

        locationRequest = LocationRequest.create().apply {
            interval = 2000L
            fastestInterval = 2000L
            priority = PRIORITY_HIGH_ACCURACY
        }


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                latitudeFlow.value = locationResult.lastLocation

            }
        }


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (intent.action) {
                ACTION_START_FUSED_SERVICE -> {

                    requestLastLocation()
                }
                ACTION_STOP_FUSED_SERVICE -> {
                    stopRequestLocation()
                }
                else -> {
                    /*NO_OP*/
                }
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun requestLastLocation() {

        if (PermissionUtil.checkPermission(applicationContext)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    latitudeFlow.value=it

                    Log.i("TAG", "requestLastLocation:${it.latitude} ")
                }
                requestCurrentLocation()
            }

        }

    }

    private fun stopRequestLocation() {
        stopSelf()
    }

    private fun requestCurrentLocation() {
        if (PermissionUtil.checkPermission(applicationContext)) {
            fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(applicationContext)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}

object Constant {
    const val ACTION_START_FUSED_SERVICE="ACTION_START_FUSED_SERVICE"
    const val ACTION_STOP_FUSED_SERVICE="ACTION_PAUSE_FUSED_SERVICE"

}
object PermissionUtil {
    fun checkPermission(applicationContext: Context) =
        (ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED)
}