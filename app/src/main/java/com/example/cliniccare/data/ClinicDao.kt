package com.example.cliniccare.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/** Data Access Object for clinic database operations using Room */
@Dao
interface ClinicDao {

    // ==================== PATIENT OPERATIONS ====================

    /** Inserts a new patient into the database, replacing on conflict */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity): Long

    /** Updates an existing patient in the database */
    @Update
    suspend fun updatePatient(patient: PatientEntity)

    /** Deletes a patient from the database */
    @Delete
    suspend fun deletePatient(patient: PatientEntity)

    /** Retrieves all patients ordered by name */
    @Query("SELECT * FROM patients ORDER BY name ASC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    /** Retrieves a patient by their ID */
    @Query("SELECT * FROM patients WHERE id = :patientId")
    fun getPatientById(patientId: Long): Flow<PatientEntity?>

    /** Searches patients by name or phone number */
    @Query("SELECT * FROM patients WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchPatients(query: String): Flow<List<PatientEntity>>

    // ==================== DOCTOR OPERATIONS ====================

    /** Inserts a new doctor into the database, replacing on conflict */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: DoctorEntity): Long

    /** Updates an existing doctor in the database */
    @Update
    suspend fun updateDoctor(doctor: DoctorEntity)

    /** Deletes a doctor from the database */
    @Delete
    suspend fun deleteDoctor(doctor: DoctorEntity)

    /** Retrieves all doctors ordered by name */
    @Query("SELECT * FROM doctors ORDER BY name ASC")
    fun getAllDoctors(): Flow<List<DoctorEntity>>

    /** Retrieves a doctor by their ID */
    @Query("SELECT * FROM doctors WHERE id = :doctorId")
    fun getDoctorById(doctorId: Long): Flow<DoctorEntity?>

    /** Retrieves doctors by specialty */
    @Query("SELECT * FROM doctors WHERE specialty = :specialty")
    fun getDoctorsBySpecialty(specialty: String): Flow<List<DoctorEntity>>

    /** Searches doctors by name or specialty */
    @Query("SELECT * FROM doctors WHERE name LIKE '%' || :query || '%' OR specialty LIKE '%' || :query || '%'")
    fun searchDoctors(query: String): Flow<List<DoctorEntity>>

    // ==================== APPOINTMENT OPERATIONS ====================

    /** Inserts a new appointment into the database, replacing on conflict */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    /** Updates an existing appointment in the database */
    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    /** Deletes an appointment from the database */
    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)

    /** Retrieves all appointments ordered by date and time descending */
    @Query("SELECT * FROM appointments ORDER BY date DESC, time DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    /** Retrieves an appointment by its ID */
    @Query("SELECT * FROM appointments WHERE id = :appointmentId")
    fun getAppointmentById(appointmentId: Long): Flow<AppointmentEntity?>

    /** Retrieves appointments for a specific patient */
    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY date DESC")
    fun getAppointmentsByPatient(patientId: Long): Flow<List<AppointmentEntity>>

    /** Retrieves appointments for a specific doctor */
    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId ORDER BY date DESC")
    fun getAppointmentsByDoctor(doctorId: Long): Flow<List<AppointmentEntity>>

    /** Retrieves appointments by status */
    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY date ASC")
    fun getAppointmentsByStatus(status: String): Flow<List<AppointmentEntity>>

    /** Retrieves appointments for a specific date */
    @Query("SELECT * FROM appointments WHERE date = :date ORDER BY time ASC")
    fun getAppointmentsByDate(date: String): Flow<List<AppointmentEntity>>

    // ==================== TREATMENT OPERATIONS ====================

    /** Inserts a new treatment into the database, replacing on conflict */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreatment(treatment: TreatmentEntity): Long

    /** Updates an existing treatment in the database */
    @Update
    suspend fun updateTreatment(treatment: TreatmentEntity)

    /** Deletes a treatment from the database */
    @Delete
    suspend fun deleteTreatment(treatment: TreatmentEntity)

    /** Retrieves all treatments ordered by date descending */
    @Query("SELECT * FROM treatments ORDER BY date DESC")
    fun getAllTreatments(): Flow<List<TreatmentEntity>>

    /** Retrieves a treatment by its ID */
    @Query("SELECT * FROM treatments WHERE id = :treatmentId")
    fun getTreatmentById(treatmentId: Long): Flow<TreatmentEntity?>

    /** Retrieves treatments for a specific patient */
    @Query("SELECT * FROM treatments WHERE patientId = :patientId ORDER BY date DESC")
    fun getTreatmentsByPatient(patientId: Long): Flow<List<TreatmentEntity>>

    /** Retrieves treatments for a specific appointment */
    @Query("SELECT * FROM treatments WHERE appointmentId = :appointmentId")
    fun getTreatmentsByAppointment(appointmentId: Long): Flow<List<TreatmentEntity>>

    // ==================== STATISTICS QUERIES ====================

    /** Returns the total number of patients */
    @Query("SELECT COUNT(*) FROM patients")
    fun getTotalPatients(): Flow<Int>

    /** Returns the total number of doctors */
    @Query("SELECT COUNT(*) FROM doctors")
    fun getTotalDoctors(): Flow<Int>

    /** Returns the count of upcoming appointments */
    @Query("SELECT COUNT(*) FROM appointments WHERE status = 'Upcoming'")
    fun getUpcomingAppointmentsCount(): Flow<Int>

    /** Returns the count of appointments for a specific date */
    @Query("SELECT COUNT(*) FROM appointments WHERE date = :date")
    fun getTodayAppointmentsCount(date: String): Flow<Int>
}
