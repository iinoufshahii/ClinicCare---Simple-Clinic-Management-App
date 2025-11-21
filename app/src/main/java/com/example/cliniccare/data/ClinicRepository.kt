package com.example.cliniccare.data

import kotlinx.coroutines.flow.Flow

/** Repository for managing clinic data operations, providing a clean API to the ViewModel */
class ClinicRepository(private val dao: ClinicDao) {

    // ==================== PATIENT OPERATIONS ====================

    /** Retrieves all patients from the database */
    fun getAllPatients(): Flow<List<PatientEntity>> = dao.getAllPatients()

    /** Retrieves a patient by ID */
    fun getPatientById(id: Long): Flow<PatientEntity?> = dao.getPatientById(id)

    /** Searches patients by query string */
    fun searchPatients(query: String): Flow<List<PatientEntity>> = dao.searchPatients(query)

    /** Inserts a new patient */
    suspend fun insertPatient(patient: PatientEntity): Long = dao.insertPatient(patient)

    /** Updates an existing patient */
    suspend fun updatePatient(patient: PatientEntity) = dao.updatePatient(patient)

    /** Deletes a patient */
    suspend fun deletePatient(patient: PatientEntity) = dao.deletePatient(patient)

    // ==================== DOCTOR OPERATIONS ====================

    /** Retrieves all doctors from the database */
    fun getAllDoctors(): Flow<List<DoctorEntity>> = dao.getAllDoctors()

    /** Retrieves a doctor by ID */
    fun getDoctorById(id: Long): Flow<DoctorEntity?> = dao.getDoctorById(id)

    /** Retrieves doctors by specialty */
    fun getDoctorsBySpecialty(specialty: String): Flow<List<DoctorEntity>> =
        dao.getDoctorsBySpecialty(specialty)

    /** Searches doctors by query string */
    fun searchDoctors(query: String): Flow<List<DoctorEntity>> = dao.searchDoctors(query)

    /** Inserts a new doctor */
    suspend fun insertDoctor(doctor: DoctorEntity): Long = dao.insertDoctor(doctor)

    /** Updates an existing doctor */
    suspend fun updateDoctor(doctor: DoctorEntity) = dao.updateDoctor(doctor)

    /** Deletes a doctor */
    suspend fun deleteDoctor(doctor: DoctorEntity) = dao.deleteDoctor(doctor)

    // ==================== APPOINTMENT OPERATIONS ====================

    /** Retrieves all appointments from the database */
    fun getAllAppointments(): Flow<List<AppointmentEntity>> = dao.getAllAppointments()

    /** Retrieves an appointment by ID */
    fun getAppointmentById(id: Long): Flow<AppointmentEntity?> = dao.getAppointmentById(id)

    /** Retrieves appointments for a specific patient */
    fun getAppointmentsByPatient(patientId: Long): Flow<List<AppointmentEntity>> =
        dao.getAppointmentsByPatient(patientId)

    /** Retrieves appointments for a specific doctor */
    fun getAppointmentsByDoctor(doctorId: Long): Flow<List<AppointmentEntity>> =
        dao.getAppointmentsByDoctor(doctorId)

    /** Retrieves appointments by status */
    fun getAppointmentsByStatus(status: String): Flow<List<AppointmentEntity>> =
        dao.getAppointmentsByStatus(status)

    /** Retrieves appointments for a specific date */
    fun getAppointmentsByDate(date: String): Flow<List<AppointmentEntity>> =
        dao.getAppointmentsByDate(date)

    /** Inserts a new appointment */
    suspend fun insertAppointment(appointment: AppointmentEntity): Long =
        dao.insertAppointment(appointment)

    /** Updates an existing appointment */
    suspend fun updateAppointment(appointment: AppointmentEntity) =
        dao.updateAppointment(appointment)

    /** Deletes an appointment */
    suspend fun deleteAppointment(appointment: AppointmentEntity) =
        dao.deleteAppointment(appointment)

    // ==================== TREATMENT OPERATIONS ====================

    /** Retrieves all treatments from the database */
    fun getAllTreatments(): Flow<List<TreatmentEntity>> = dao.getAllTreatments()

    /** Retrieves a treatment by ID */
    fun getTreatmentById(id: Long): Flow<TreatmentEntity?> = dao.getTreatmentById(id)

    /** Retrieves treatments for a specific patient */
    fun getTreatmentsByPatient(patientId: Long): Flow<List<TreatmentEntity>> =
        dao.getTreatmentsByPatient(patientId)

    /** Retrieves treatments for a specific appointment */
    fun getTreatmentsByAppointment(appointmentId: Long): Flow<List<TreatmentEntity>> =
        dao.getTreatmentsByAppointment(appointmentId)

    /** Inserts a new treatment */
    suspend fun insertTreatment(treatment: TreatmentEntity): Long = dao.insertTreatment(treatment)

    /** Updates an existing treatment */
    suspend fun updateTreatment(treatment: TreatmentEntity) = dao.updateTreatment(treatment)

    /** Deletes a treatment */
    suspend fun deleteTreatment(treatment: TreatmentEntity) = dao.deleteTreatment(treatment)

    // ==================== STATISTICS ====================

    /** Retrieves the total number of patients */
    fun getTotalPatients(): Flow<Int> = dao.getTotalPatients()

    /** Retrieves the total number of doctors */
    fun getTotalDoctors(): Flow<Int> = dao.getTotalDoctors()

    /** Retrieves the count of upcoming appointments */
    fun getUpcomingAppointmentsCount(): Flow<Int> = dao.getUpcomingAppointmentsCount()

    /** Retrieves the count of appointments for today */
    fun getTodayAppointmentsCount(date: String): Flow<Int> = dao.getTodayAppointmentsCount(date)
}
