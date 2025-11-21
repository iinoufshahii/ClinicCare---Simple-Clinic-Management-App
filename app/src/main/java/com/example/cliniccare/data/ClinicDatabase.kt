package com.example.cliniccare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** Room database for the clinic application containing all entities */
@Database(
    entities = [
        PatientEntity::class,
        DoctorEntity::class,
        AppointmentEntity::class,
        TreatmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ClinicDatabase : RoomDatabase() {

    /** Provides access to the DAO for database operations */
    abstract fun clinicDao(): ClinicDao

    companion object {
        /** Volatile instance for thread-safe singleton */
        @Volatile
        private var INSTANCE: ClinicDatabase? = null

        /** Returns the singleton instance of the database, creating it if necessary */
        fun getDatabase(context: Context): ClinicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClinicDatabase::class.java,
                    "clinic_database"
                )
                    .fallbackToDestructiveMigration() // For development; remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
