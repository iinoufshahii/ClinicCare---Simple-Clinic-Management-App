package com.example.cliniccare.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

/** Room entity representing a patient stored in the database */
@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: String, // Store as String for Room compatibility
    val dateOfBirth: String,
    val age: Int,
    val phone: String,
    val address: String
)

/** Room entity representing a doctor stored in the database */
@Entity(tableName = "doctors")
data class DoctorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val specialty: String,
    val phone: String,
    val email: String
)

/** Room entity representing an appointment with foreign key constraints to patients and doctors */
@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DoctorEntity::class,
            parentColumns = ["id"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId"), Index("doctorId")]
)
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val doctorId: Long,
    val date: String,
    val time: String,
    val reason: String,
    val status: String // Store as String for Room compatibility
)

/** Room entity representing a treatment with foreign key constraints to appointments and patients */
@Entity(
    tableName = "treatments",
    foreignKeys = [
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("appointmentId"), Index("patientId")]
)
data class TreatmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appointmentId: Long,
    val patientId: Long,
    val diagnosis: String,
    val treatment: String,
    val notes: String,
    val date: String
)

// Extension functions to convert between Entity and UI Model

/** Converts a PatientEntity to a Patient UI model */
fun PatientEntity.toPatient() = Patient(
    id = id.toString(),
    name = name,
    gender = Gender.valueOf(gender),
    dateOfBirth = dateOfBirth,
    age = age,
    phone = phone,
    address = address
)

/** Converts a Patient UI model to a PatientEntity */
fun Patient.toEntity() = PatientEntity(
    id = if (id.isNotEmpty() && id != "0") id.toLong() else 0,
    name = name,
    gender = gender.name,
    dateOfBirth = dateOfBirth,
    age = age,
    phone = phone,
    address = address
)

/** Converts a DoctorEntity to a Doctor UI model */
fun DoctorEntity.toDoctor() = Doctor(
    id = id.toString(),
    name = name,
    specialty = specialty,
    phone = phone,
    email = email
)

/** Converts a Doctor UI model to a DoctorEntity */
fun Doctor.toEntity() = DoctorEntity(
    id = if (id.isNotEmpty() && id != "0") id.toLong() else 0,
    name = name,
    specialty = specialty,
    phone = phone,
    email = email
)

/** Converts an AppointmentEntity to an Appointment UI model */
fun AppointmentEntity.toAppointment() = Appointment(
    id = id.toString(),
    patientId = patientId.toString(),
    doctorId = doctorId.toString(),
    date = date,
    time = time,
    reason = reason,
    status = AppointmentStatus.valueOf(status)
)

/** Converts an Appointment UI model to an AppointmentEntity */
fun Appointment.toEntity() = AppointmentEntity(
    id = if (id.isNotEmpty() && id != "0") id.toLong() else 0,
    patientId = patientId.toLong(),
    doctorId = doctorId.toLong(),
    date = date,
    time = time,
    reason = reason,
    status = status.name
)

/** Converts a TreatmentEntity to a Treatment UI model */
fun TreatmentEntity.toTreatment() = Treatment(
    id = id.toString(),
    appointmentId = appointmentId.toString(),
    patientId = patientId.toString(),
    diagnosis = diagnosis,
    treatment = treatment,
    notes = notes,
    date = date
)

/** Converts a Treatment UI model to a TreatmentEntity */
fun Treatment.toEntity() = TreatmentEntity(
    id = if (id.isNotEmpty() && id != "0") id.toLong() else 0,
    appointmentId = appointmentId.toLong(),
    patientId = patientId.toLong(),
    diagnosis = diagnosis,
    treatment = treatment,
    notes = notes,
    date = date
)
