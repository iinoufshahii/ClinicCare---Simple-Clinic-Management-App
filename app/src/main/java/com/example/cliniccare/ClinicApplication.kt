package com.example.cliniccare

import android.app.Application
import com.example.cliniccare.data.ClinicDatabase
import com.example.cliniccare.data.ClinicRepository

/** Application class for the ClinicCare app, initializes database and repository */
class ClinicApplication : Application() {
    /** Lazy initialization of the database */
    val database by lazy { ClinicDatabase.getDatabase(this) }
    /** Lazy initialization of the repository */
    val repository by lazy { ClinicRepository(database.clinicDao()) }
}
