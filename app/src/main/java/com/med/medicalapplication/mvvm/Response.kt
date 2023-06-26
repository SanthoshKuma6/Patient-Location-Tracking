package com.med.medicalapplication.mvvm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Response {
    @Insert
    suspend fun insert(patient: PatientModelClass)
    @Query("select * from Patient")
    fun getList(): Flow<List<PatientModelClass>>


    //location
    @Insert
   suspend fun insertLocation(location: LocationTable)
    @Query("select * from location_table")
    fun getLocation():Flow<List<LocationTable>>
}