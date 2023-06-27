package com.med.medicalapplication.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.med.medicalapplication.R
import com.med.medicalapplication.activity.adapter.PatientAdapter
import com.med.medicalapplication.databinding.ActivityMainBinding
import com.med.medicalapplication.location.Constant
import com.med.medicalapplication.location.Constant.ACTION_STOP_FUSED_SERVICE
import com.med.medicalapplication.location.FusedLocationService
import com.med.medicalapplication.location.displayLocationSettingsRequest
import com.med.medicalapplication.location.getLocationStatus
import com.med.medicalapplication.mvvm.DataBase
import com.med.medicalapplication.mvvm.model.LocationTable
import com.med.medicalapplication.mvvm.PatientFactory
import com.med.medicalapplication.mvvm.model.PatientModelClass
import com.med.medicalapplication.mvvm.PatientRepository
import com.med.medicalapplication.mvvm.PatientViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val mainActivity by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var viewModel: PatientViewModel
    private lateinit var patientAdapter: PatientAdapter
    private var hasLocationPermission = false
    private var latitude = ""
    private var longitude = ""
    private var bearing = ""
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainActivity.root)
        val repository = PatientRepository(DataBase.getDatabase(this))
        val factory = PatientFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PatientViewModel::class.java]
        patientAdapter = PatientAdapter()
        viewData()
        getLocation()

        mainActivity.addFab.setOnClickListener {
//            viewModel.insertLocation(LocationTable(lat,long))

//            saveData()
            patientDetails()


        }
        mainActivity.mapView.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)


        }
    }



    private fun patientDetails() {
        val builder = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.alert_popum, null)
        builder.setView(customLayout)
        builder.setPositiveButton("Submit") { _: DialogInterface?, _: Int ->
            val patientName = customLayout.findViewById<EditText>(R.id.name)
            val patientId = customLayout.findViewById<EditText>(R.id.id)
            val patientAge = customLayout.findViewById<EditText>(R.id.age)
            val patientGender = customLayout.findViewById<EditText>(R.id.gender)
            if (patientName.editableText.toString().isEmpty()) {
                Toast.makeText(this@MainActivity, "enter your name", Toast.LENGTH_SHORT).show()

            } else if (patientAge.editableText.toString().isEmpty()) {
                Toast.makeText(this@MainActivity, "enter your age", Toast.LENGTH_SHORT).show()

            } else if (patientId.editableText.toString().isEmpty()) {
                Toast.makeText(this@MainActivity, "enter your id", Toast.LENGTH_SHORT).show()

            } else if (patientGender.editableText.toString().isEmpty()) {
                Toast.makeText(this@MainActivity, "enter your Gender", Toast.LENGTH_SHORT).show()

            } else {
                sendDialogDataToActivity(
                    patientName.text.toString(),
                    patientId.text.toString(),
                    patientAge.text.toString(),
                    patientGender.text.toString()
                )
                lifecycleScope.launch {
                    viewModel.insertLocation(
                        LocationTable(
                            latitude,
                            longitude
                        )
                    )

                }

            }

        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun sendDialogDataToActivity(name: String, id: String, age: String, gender: String) {
        lifecycleScope.launch {
            viewModel.insert(
                PatientModelClass(
                    name, id, age, gender
                )
            )
            Toast.makeText(this@MainActivity, "Save Successfully", Toast.LENGTH_SHORT)
                .show()
        }


    }

//    private fun saveData() {
//        mainActivity.apply {
//            if (name.text.toString().isEmpty()) {
//                Toast.makeText(this@MainActivity, "enter your name", Toast.LENGTH_SHORT).show()
//            } else if (id.text.toString().isEmpty()) {
//                Toast.makeText(this@MainActivity, "enter your id", Toast.LENGTH_SHORT).show()
//            } else if (age.text.toString().isEmpty()) {
//                Toast.makeText(this@MainActivity, "enter your age", Toast.LENGTH_SHORT).show()
//            } else if (gender.text.toString().isEmpty()) {
//                Toast.makeText(this@MainActivity, "enter your gender", Toast.LENGTH_SHORT).show()
//            } else {
//                lifecycleScope.launchWhenCreated {
//                    viewModel.insert(
//                        PatientModelClass(
//                            name.text.toString(),
//                            id.text.toString(),
//                            age.text.toString(),
//                            gender.text.toString()
//                        )
//                    )
//                    Toast.makeText(this@MainActivity, "Save Successfully", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//
//            }
//        }
//
//    }

    private fun viewData() {

        lifecycleScope.launch {
            viewModel.getList().collect {
                mainActivity.apply {
                    recyclerView.adapter = patientAdapter
                }
                patientAdapter.differ.submitList(it)
            }
        }
    }




    private fun getLocation() {

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->

                hasLocationPermission =
                    permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: hasLocationPermission

                if (!hasLocationPermission) {
                    finish()

                } else {
                    showLocationServicePermission()
                    startLocationService()
                }

            }

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
            )
        )
        FusedLocationService.latitudeFlow.observe(this) {
//            Toast.makeText(this,"$lat,$long",Toast.LENGTH_SHORT).show()
            latitude = it.latitude.toString()
            longitude = it.longitude.toString()
            bearing = it.bearing.toString()

        }
    }

    private fun showLocationServicePermission() {
        if (!getLocationStatus(this)) {
            displayLocationSettingsRequest(this, this)
        }
    }

    private fun startLocationService() {

        Intent(this, FusedLocationService::class.java).also {
            it.action = Constant.ACTION_START_FUSED_SERVICE
            startService(it)
        }

    }

    private fun stopLocationServices() {

        Intent(this, FusedLocationService::class.java).also {
            it.action = ACTION_STOP_FUSED_SERVICE
            startService(it)
        }

    }

    override fun onResume() {
        super.onResume()
        startLocationService()
    }

    override fun onPause() {
        super.onPause()
        stopLocationServices()
    }

}