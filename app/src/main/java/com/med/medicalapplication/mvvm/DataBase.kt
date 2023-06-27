package com.med.medicalapplication.mvvm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.med.medicalapplication.mvvm.model.LocationTable
import com.med.medicalapplication.mvvm.model.PatientModelClass

@Database(
    entities = [PatientModelClass::class, LocationTable::class],
    version = 3,
    exportSchema = false
)
abstract class DataBase:RoomDatabase() {
    abstract fun setResponse():Response
    companion object {
        private var Instant: DataBase? = null
        fun getDatabase(context: Context): DataBase {
            return Instant ?: synchronized(this) {
                val instant = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "Data Base"
                ).fallbackToDestructiveMigration()
                    .build()
                Instant = instant
                instant
            }
        }
    }


}