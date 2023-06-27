package com.med.medicalapplication.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.med.medicalapplication.mvvm.model.LocationTable
import com.med.medicalapplication.mvvm.model.PatientModelClass
import kotlinx.coroutines.launch

class PatientViewModel(private val repository: PatientRepository):ViewModel() {
    suspend fun insert(model: PatientModelClass) = viewModelScope.launch {
        repository.insert(model)
    }
    fun getList() = repository.getList()

    //location
    suspend fun insertLocation(locationTable: LocationTable) = viewModelScope.launch {
        repository.insertLocation(locationTable)
    }
    fun getLocation()=repository.getLocation()

}