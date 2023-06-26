package com.med.medicalapplication.mvvm

class PatientRepository(private val dataBase: DataBase) {
    suspend fun insert(model:PatientModelClass)=dataBase.setResponse().insert(model)
    fun getList()=dataBase.setResponse().getList()

    //location
  suspend  fun insertLocation(locationTable: LocationTable)= dataBase.setResponse().insertLocation(locationTable)
    fun getLocation()=dataBase.setResponse().getLocation()

}