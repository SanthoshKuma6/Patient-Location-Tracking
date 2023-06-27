package com.med.medicalapplication.mvvm.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize



@Parcelize
@Entity(tableName = "location_table")
data class LocationTable (
    val lat:String,
    val lan:String,
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,

):Parcelable
