package com.example.cliniccare.data

/** Represents a patient in the clinic system */
data class Patient(
    val id: String,
    val name: String,
    val gender: Gender,
    val dateOfBirth: String,
    val age: Int,
    val phone: String,
    val address: String
)

/** Enumeration of possible gender values for patients */
enum class Gender {
    Male, Female, Other
}

/** Represents a doctor in the clinic system */
data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val phone: String,
    val email: String
)

/** Represents an appointment scheduled in the clinic */
data class Appointment(
    val id: String,
    val patientId: String,
    val doctorId: String,
    val date: String,
    val time: String,
    val reason: String,
    val status: AppointmentStatus
)

/** Enumeration of possible appointment statuses */
enum class AppointmentStatus {
    Upcoming, Completed, Cancelled
}

/** Represents a treatment record associated with an appointment */
data class Treatment(
    val id: String,
    val appointmentId: String,
    val patientId: String,
    val diagnosis: String,
    val treatment: String,
    val notes: String,
    val date: String
)
