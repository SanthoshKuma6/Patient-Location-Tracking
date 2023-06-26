package com.med.medicalapplication.location

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes

object LocationUtil
private const val TAG = "LocationUtil"

fun getLocationStatus(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        locationManager.isLocationEnabled
    } else {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}
fun displayLocationSettingsRequest(context: Context, activity: Activity) {
    val googleApiClient =
        GoogleApiClient.Builder(context).addApi(LocationServices.API).build()

    googleApiClient.connect()

    val locationRequest = LocationRequest.create()
    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    locationRequest.interval = 10000
    locationRequest.fastestInterval = 10000 / 2.toLong()

    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    builder.setAlwaysShow(true)

    val pendingResult: PendingResult<LocationSettingsResult> =
        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
    pendingResult.setResultCallback { result ->
        val status: Status = result.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> Log.i(
                TAG,
                "All location settings are satisfied."
            )
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                Log.i(
                    TAG,
                    "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                )
                try {
                    status.startResolutionForResult(activity, 2)
                } catch (e: IntentSender.SendIntentException) {
                    Log.i(TAG, "PendingIntent unable to execute request.")
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                TAG,
                "Location settings are inadequate, and cannot be fixed here. Dialog not created."
            )
        }
    }
}