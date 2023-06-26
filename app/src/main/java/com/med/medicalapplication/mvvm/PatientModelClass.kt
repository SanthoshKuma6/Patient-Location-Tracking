package com.med.medicalapplication.mvvm

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "Patient")
data class PatientModelClass(
    var patient_name:String?=null,
    var patient_id:String?=null,
    var age:String?=null,
    var gender:String?=null,
    val lat:String?=null,
    val lan:String?=null,
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,

): Parcelable
