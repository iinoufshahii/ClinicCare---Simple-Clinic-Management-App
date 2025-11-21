package com.example.cliniccare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliniccare.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

/** ViewModel for managing clinic data and business logic */
class ClinicViewModel(private val repository: ClinicRepository) : ViewModel() {

    // ==================== STATE FLOWS ====================

    /** StateFlow for the list of patients, mapped from entities */
    val patientList: StateFlow<List<Patient>> = repository.getAllPatients()
        .map { entities -> entities.map { it.toPatient() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** StateFlow for the list of doctors, mapped from entities */
    val doctorList: StateFlow<List<Doctor>> = repository.getAllDoctors()
        .map { entities -> entities.map { it.toDoctor() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** StateFlow for the list of appointments, mapped from entities */
    val appointmentList: StateFlow<List<Appointment>> = repository.getAllAppointments()
        .map { entities -> entities.map { it.toAppointment() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** StateFlow for the list of treatments, mapped from entities */
    val treatmentList: StateFlow<List<Treatment>> = repository.getAllTreatments()
        .map { entities -> entities.map { it.toTreatment() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** StateFlow for the total number of patients */
    val totalPatients: StateFlow<Int> = repository.getTotalPatients()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    /** StateFlow for the total number of doctors */
    val totalDoctors: StateFlow<Int> = repository.getTotalDoctors()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    /** StateFlow for the count of upcoming appointments */
    val upcomingAppointmentsCount: StateFlow<Int> = repository.getUpcomingAppointmentsCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Add new derived counts
    /** StateFlow for the count of treatments */
    val treatmentCount: StateFlow<Int> = treatmentList
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Today's date in unified format yyyy-MM-dd (ensure UI uses this format for appointments)
    private val todayDateString = try { java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) } catch (e: Exception) { "" }

    /** StateFlow for the count of today's appointments */
    val todayAppointmentsCount: StateFlow<Int> = repository.getTodayAppointmentsCount(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Toast/Message State
    private val _toastMessage = MutableStateFlow<String?>(null)
    /** StateFlow for displaying toast messages */
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Loading State
    private val _isLoading = MutableStateFlow(false)
    /** StateFlow for indicating loading state */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ==================== PATIENT OPERATIONS ====================

    /** Add a new patient */
    fun addPatient(
        name: String,
        age: Int,
        gender: Gender,
        dateOfBirth: String,
        phone: String,
        address: String
    ) {
        viewModelScope.launch {
            try {
                // Validation
                if (name.isBlank()) {
                    _toastMessage.value = "Error: Name cannot be empty"
                    return@launch
                }
                if (phone.isBlank()) {
                    _toastMessage.value = "Error: Phone cannot be empty"
                    return@launch
                }
                if (dateOfBirth.isBlank()) {
                    _toastMessage.value = "Error: Date of birth cannot be empty"
                    return@launch
                }
                if (address.isBlank()) {
                    _toastMessage.value = "Error: Address cannot be empty"
                    return@launch
                }
                if (age <= 0) {
                    _toastMessage.value = "Error: Age must be positive"
                    return@launch
                }

                _isLoading.value = true

                val patient = PatientEntity(
                    name = name.trim(),
                    age = age,
                    gender = gender.name,
                    dateOfBirth = dateOfBirth.trim(),
                    phone = phone.trim(),
                    address = address.trim()
                )

                repository.insertPatient(patient)
                _toastMessage.value = "Success: Patient Added!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Update an existing patient */
    fun updatePatient(
        id: String,
        name: String,
        age: Int,
        gender: Gender,
        dateOfBirth: String,
        phone: String,
        address: String
    ) {
        viewModelScope.launch {
            try {
                // Validation
                if (name.isBlank()) {
                    _toastMessage.value = "Error: Name cannot be empty"
                    return@launch
                }
                if (phone.isBlank()) {
                    _toastMessage.value = "Error: Phone cannot be empty"
                    return@launch
                }
                if (dateOfBirth.isBlank()) {
                    _toastMessage.value = "Error: Date of birth cannot be empty"
                    return@launch
                }
                if (address.isBlank()) {
                    _toastMessage.value = "Error: Address cannot be empty"
                    return@launch
                }
                if (age <= 0) {
                    _toastMessage.value = "Error: Age must be positive"
                    return@launch
                }

                _isLoading.value = true

                val patient = PatientEntity(
                    id = id.toLong(),
                    name = name.trim(),
                    age = age,
                    gender = gender.name,
                    dateOfBirth = dateOfBirth.trim(),
                    phone = phone.trim(),
                    address = address.trim()
                )

                repository.updatePatient(patient)
                _toastMessage.value = "Success: Patient Updated!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Delete a patient */
    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deletePatient(patient.toEntity())
                _toastMessage.value = "Success: Patient Deleted!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Get a patient by ID */
    fun getPatientById(id: String): Flow<Patient?> {
        return repository.getPatientById(id.toLong())
            .map { it?.toPatient() }
    }

    /** Search for patients by query */
    fun searchPatients(query: String): Flow<List<Patient>> {
        return repository.searchPatients(query)
            .map { entities -> entities.map { it.toPatient() } }
    }

    // ==================== DOCTOR OPERATIONS ====================

    /** Add a new doctor */
    fun addDoctor(
        name: String,
        specialty: String,
        phone: String,
        email: String
    ) {
        viewModelScope.launch {
            try {
                // Validation
                if (name.isBlank()) {
                    _toastMessage.value = "Error: Name cannot be empty"
                    return@launch
                }
                if (specialty.isBlank()) {
                    _toastMessage.value = "Error: Specialty cannot be empty"
                    return@launch
                }
                if (phone.isBlank()) {
                    _toastMessage.value = "Error: Phone cannot be empty"
                    return@launch
                }
                if (email.isBlank()) {
                    _toastMessage.value = "Error: Email cannot be empty"
                    return@launch
                }
                if (!email.contains("@")) {
                    _toastMessage.value = "Error: Invalid email format"
                    return@launch
                }

                _isLoading.value = true

                val doctor = DoctorEntity(
                    name = name.trim(),
                    specialty = specialty.trim(),
                    phone = phone.trim(),
                    email = email.trim()
                )

                repository.insertDoctor(doctor)
                _toastMessage.value = "Success: Doctor Added!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Update an existing doctor */
    fun updateDoctor(
        id: String,
        name: String,
        specialty: String,
        phone: String,
        email: String
    ) {
        viewModelScope.launch {
            try {
                // Validation
                if (name.isBlank()) {
                    _toastMessage.value = "Error: Name cannot be empty"
                    return@launch
                }
                if (specialty.isBlank()) {
                    _toastMessage.value = "Error: Specialty cannot be empty"
                    return@launch
                }
                if (phone.isBlank()) {
                    _toastMessage.value = "Error: Phone cannot be empty"
                    return@launch
                }
                if (email.isBlank()) {
                    _toastMessage.value = "Error: Email cannot be empty"
                    return@launch
                }
                if (!email.contains("@")) {
                    _toastMessage.value = "Error: Invalid email format"
                    return@launch
                }

                _isLoading.value = true

                val doctor = DoctorEntity(
                    id = id.toLong(),
                    name = name.trim(),
                    specialty = specialty.trim(),
                    phone = phone.trim(),
                    email = email.trim()
                )

                repository.updateDoctor(doctor)
                _toastMessage.value = "Success: Doctor Updated!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Delete a doctor */
    fun deleteDoctor(doctor: Doctor) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteDoctor(doctor.toEntity())
                _toastMessage.value = "Success: Doctor Deleted!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Get a doctor by ID */
    fun getDoctorById(id: String): Flow<Doctor?> {
        return repository.getDoctorById(id.toLong())
            .map { it?.toDoctor() }
    }

    /** Search for doctors by query */
    fun searchDoctors(query: String): Flow<List<Doctor>> {
        return repository.searchDoctors(query)
            .map { entities -> entities.map { it.toDoctor() } }
    }

    // ==================== APPOINTMENT OPERATIONS ====================

    /** Schedule a new appointment */
    fun addAppointment(
        patientId: String,
        doctorId: String,
        date: String,
        time: String,
        reason: String,
        status: AppointmentStatus = AppointmentStatus.Upcoming
    ) {
        viewModelScope.launch {
            try {
                // Validation
                if (patientId.isBlank() || patientId == "0") {
                    _toastMessage.value = "Error: Please select a patient"
                    return@launch
                }
                if (doctorId.isBlank() || doctorId == "0") {
                    _toastMessage.value = "Error: Please select a doctor"
                    return@launch
                }
                if (date.isBlank()) {
                    _toastMessage.value = "Error: Date cannot be empty"
                    return@launch
                }
                if (time.isBlank()) {
                    _toastMessage.value = "Error: Time cannot be empty"
                    return@launch
                }
                if (reason.isBlank()) {
                    _toastMessage.value = "Error: Reason cannot be empty"
                    return@launch
                }

                _isLoading.value = true

                val appointment = AppointmentEntity(
                    patientId = patientId.toLong(),
                    doctorId = doctorId.toLong(),
                    date = date.trim(),
                    time = time.trim(),
                    reason = reason.trim(),
                    status = status.name
                )

                repository.insertAppointment(appointment)
                _toastMessage.value = "Success: Appointment Scheduled!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Update an existing appointment */
    fun updateAppointment(
        id: String,
        patientId: String,
        doctorId: String,
        date: String,
        time: String,
        reason: String,
        status: AppointmentStatus
    ) {
        viewModelScope.launch {
            try {
                // Validation
                if (patientId.isBlank() || patientId == "0") {
                    _toastMessage.value = "Error: Please select a patient"
                    return@launch
                }
                if (doctorId.isBlank() || doctorId == "0") {
                    _toastMessage.value = "Error: Please select a doctor"
                    return@launch
                }
                if (date.isBlank()) {
                    _toastMessage.value = "Error: Date cannot be empty"
                    return@launch
                }
                if (time.isBlank()) {
                    _toastMessage.value = "Error: Time cannot be empty"
                    return@launch
                }
                if (reason.isBlank()) {
                    _toastMessage.value = "Error: Reason cannot be empty"
                    return@launch
                }

                _isLoading.value = true

                val appointment = AppointmentEntity(
                    id = id.toLong(),
                    patientId = patientId.toLong(),
                    doctorId = doctorId.toLong(),
                    date = date.trim(),
                    time = time.trim(),
                    reason = reason.trim(),
                    status = status.name
                )

                repository.updateAppointment(appointment)
                _toastMessage.value = "Success: Appointment Updated!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Delete an appointment */
    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteAppointment(appointment.toEntity())
                _toastMessage.value = "Success: Appointment Deleted!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Get appointments by patient ID */
    fun getAppointmentsByPatient(patientId: String): Flow<List<Appointment>> {
        return repository.getAppointmentsByPatient(patientId.toLong())
            .map { entities -> entities.map { it.toAppointment() } }
    }

    /** Get appointments by doctor ID */
    fun getAppointmentsByDoctor(doctorId: String): Flow<List<Appointment>> {
        return repository.getAppointmentsByDoctor(doctorId.toLong())
            .map { entities -> entities.map { it.toAppointment() } }
    }

    /** Get appointments by status */
    fun getAppointmentsByStatus(status: AppointmentStatus): Flow<List<Appointment>> {
        return repository.getAppointmentsByStatus(status.name)
            .map { entities -> entities.map { it.toAppointment() } }
    }

    // Get single appointment by id mapped to domain model
    /** Get an appointment by ID */
    fun getAppointmentById(id: String): Flow<Appointment?> {
        return repository.getAppointmentById(id.toLong())
            .map { it?.toAppointment() }
    }

    // Upcoming appointments count per doctor
    /** Get the count of upcoming appointments for a doctor */
    fun getUpcomingAppointmentsCountForDoctor(doctorId: String): Flow<Int> {
        return repository.getAppointmentsByDoctor(doctorId.toLong())
            .map { list -> list.count { it.status == AppointmentStatus.Upcoming.name } }
    }

    // ==================== TREATMENT OPERATIONS ====================

    /** Record a new treatment */
    fun addTreatment(
        appointmentId: String,
        patientId: String,
        diagnosis: String,
        treatment: String,
        notes: String,
        date: String
    ) {
        viewModelScope.launch {
            try {
                // Validation
                if (appointmentId.isBlank() || appointmentId == "0") {
                    _toastMessage.value = "Error: Invalid appointment"
                    return@launch
                }
                if (patientId.isBlank() || patientId == "0") {
                    _toastMessage.value = "Error: Invalid patient"
                    return@launch
                }
                if (diagnosis.isBlank()) {
                    _toastMessage.value = "Error: Diagnosis cannot be empty"
                    return@launch
                }
                if (treatment.isBlank()) {
                    _toastMessage.value = "Error: Treatment cannot be empty"
                    return@launch
                }
                if (date.isBlank()) {
                    _toastMessage.value = "Error: Date cannot be empty"
                    return@launch
                }

                _isLoading.value = true

                val treatmentEntity = TreatmentEntity(
                    appointmentId = appointmentId.toLong(),
                    patientId = patientId.toLong(),
                    diagnosis = diagnosis.trim(),
                    treatment = treatment.trim(),
                    notes = notes.trim(),
                    date = date.trim()
                )

                repository.insertTreatment(treatmentEntity)
                _toastMessage.value = "Success: Treatment Recorded!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Update an existing treatment */
    fun updateTreatment(
        id: String,
        appointmentId: String,
        patientId: String,
        diagnosis: String,
        treatment: String,
        notes: String,
        date: String
    ) {
        viewModelScope.launch {
            try {
                // Validation
                if (diagnosis.isBlank()) {
                    _toastMessage.value = "Error: Diagnosis cannot be empty"
                    return@launch
                }
                if (treatment.isBlank()) {
                    _toastMessage.value = "Error: Treatment cannot be empty"
                    return@launch
                }
                if (date.isBlank()) {
                    _toastMessage.value = "Error: Date cannot be empty"
                    return@launch
                }

                _isLoading.value = true

                val treatmentEntity = TreatmentEntity(
                    id = id.toLong(),
                    appointmentId = appointmentId.toLong(),
                    patientId = patientId.toLong(),
                    diagnosis = diagnosis.trim(),
                    treatment = treatment.trim(),
                    notes = notes.trim(),
                    date = date.trim()
                )

                repository.updateTreatment(treatmentEntity)
                _toastMessage.value = "Success: Treatment Updated!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Delete a treatment */
    fun deleteTreatment(treatment: Treatment) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteTreatment(treatment.toEntity())
                _toastMessage.value = "Success: Treatment Deleted!"
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Get treatments by patient ID */
    fun getTreatmentsByPatient(patientId: String): Flow<List<Treatment>> {
        return repository.getTreatmentsByPatient(patientId.toLong())
            .map { entities -> entities.map { it.toTreatment() } }
    }

    // ==================== UTILITY FUNCTIONS ====================

    /** Clear the toast message */
    fun clearToast() {
        _toastMessage.value = null
    }

    /** Calculate age from date of birth */
    open fun calculateAge(dateOfBirth: String): Int {
        val patterns = listOf("dd/MM/yyyy", "yyyy-MM-dd", "d/M/yyyy")
        for (p in patterns) {
            try {
                val formatter = java.time.format.DateTimeFormatter.ofPattern(p)
                val birthDate = java.time.LocalDate.parse(dateOfBirth.trim(), formatter)
                val currentDate = java.time.LocalDate.now()
                return java.time.Period.between(birthDate, currentDate).years
            } catch (_: Exception) { /* try next */ }
        }
        return 0
    }
}
