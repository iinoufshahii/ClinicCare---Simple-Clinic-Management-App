package com.example.cliniccare.navigation

/** Sealed class representing navigation screens in the app */
sealed class Screen(val route: String) {
    /** Splash screen */
    object Splash : Screen("splash")
    /** Dashboard screen */
    object Dashboard : Screen("dashboard")
    /** Patient list screen */
    object PatientList : Screen("patient_list")
    /** Patient detail screen with ID parameter */
    object PatientDetail : Screen("patient_detail/{patientId}") {
        fun createRoute(patientId: String) = "patient_detail/$patientId"
    }
    /** Add patient screen */
    object AddPatient : Screen("add_patient")
    /** Edit patient screen with ID parameter */
    object EditPatient : Screen("edit_patient/{patientId}") {
        fun createRoute(patientId: String) = "edit_patient/$patientId"
    }
    /** Doctor list screen */
    object DoctorList : Screen("doctor_list")
    /** Doctor detail screen with ID parameter */
    object DoctorDetail : Screen("doctor_detail/{doctorId}") {
        fun createRoute(doctorId: String) = "doctor_detail/$doctorId"
    }
    /** Add doctor screen */
    object AddDoctor : Screen("add_doctor")
    /** Edit doctor screen with ID parameter */
    object EditDoctor : Screen("edit_doctor/{doctorId}") {
        fun createRoute(doctorId: String) = "edit_doctor/$doctorId"
    }
    /** Appointment list screen */
    object AppointmentList : Screen("appointment_list")
    /** Appointment detail screen with ID parameter */
    object AppointmentDetail : Screen("appointment_detail/{appointmentId}") {
        fun createRoute(appointmentId: String) = "appointment_detail/$appointmentId"
    }
    /** Add appointment screen */
    object AddAppointment : Screen("add_appointment")
    /** Edit appointment screen with ID parameter */
    object EditAppointment : Screen("edit_appointment/{appointmentId}") {
        fun createRoute(appointmentId: String) = "edit_appointment/$appointmentId"
    }
    /** Treatment history screen */
    object TreatmentHistory : Screen("treatment_history")
    /** Add treatment screen with optional appointment ID parameter */
    object AddTreatment : Screen("add_treatment?appointmentId={appointmentId}") {
        fun createRoute(appointmentId: String? = null) =
            if (appointmentId != null) "add_treatment?appointmentId=$appointmentId"
            else "add_treatment"
    }
}
