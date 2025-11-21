package com.example.cliniccare.data

// Mock Data for the application
object MockData {
    /** Sample patients for testing and demonstration purposes */
    val patients = listOf(
        Patient(
            id = "P001",
            name = "Sarah Johnson",
            gender = Gender.Female,
            dateOfBirth = "1992-03-15",
            age = 32,
            phone = "+1 555-0101",
            address = "123 Oak Street, Springfield, IL 62701"
        ),
        Patient(
            id = "P002",
            name = "Michael Chen",
            gender = Gender.Male,
            dateOfBirth = "1985-07-22",
            age = 39,
            phone = "+1 555-0102",
            address = "456 Maple Avenue, Springfield, IL 62702"
        ),
        Patient(
            id = "P003",
            name = "Emily Rodriguez",
            gender = Gender.Female,
            dateOfBirth = "1998-11-08",
            age = 26,
            phone = "+1 555-0103",
            address = "789 Pine Road, Springfield, IL 62703"
        ),
        Patient(
            id = "P004",
            name = "James Williams",
            gender = Gender.Male,
            dateOfBirth = "1975-05-30",
            age = 49,
            phone = "+1 555-0104",
            address = "321 Elm Boulevard, Springfield, IL 62704"
        ),
        Patient(
            id = "P005",
            name = "Olivia Brown",
            gender = Gender.Female,
            dateOfBirth = "2001-09-12",
            age = 23,
            phone = "+1 555-0105",
            address = "654 Cedar Lane, Springfield, IL 62705"
        )
    )

    /** Sample doctors for testing and demonstration purposes */
    val doctors = listOf(
        Doctor(
            id = "D001",
            name = "Dr. Robert Anderson",
            specialty = "Cardiologist",
            phone = "+1 555-0201",
            email = "r.anderson@clinicare.com"
        ),
        Doctor(
            id = "D002",
            name = "Dr. Lisa Martinez",
            specialty = "General Practitioner",
            phone = "+1 555-0202",
            email = "l.martinez@clinicare.com"
        ),
        Doctor(
            id = "D003",
            name = "Dr. David Kumar",
            specialty = "Pediatrician",
            phone = "+1 555-0203",
            email = "d.kumar@clinicare.com"
        ),
        Doctor(
            id = "D004",
            name = "Dr. Jennifer Lee",
            specialty = "Dermatologist",
            phone = "+1 555-0204",
            email = "j.lee@clinicare.com"
        )
    )

    /** Sample appointments for testing and demonstration purposes */
    val appointments = listOf(
        Appointment(
            id = "A001",
            patientId = "P001",
            doctorId = "D001",
            date = "2025-11-22",
            time = "09:00",
            reason = "Regular cardiac checkup",
            status = AppointmentStatus.Upcoming
        ),
        Appointment(
            id = "A002",
            patientId = "P002",
            doctorId = "D002",
            date = "2025-11-20",
            time = "10:30",
            reason = "Follow-up consultation",
            status = AppointmentStatus.Upcoming
        ),
        Appointment(
            id = "A003",
            patientId = "P003",
            doctorId = "D003",
            date = "2025-11-18",
            time = "14:00",
            reason = "Annual physical examination",
            status = AppointmentStatus.Completed
        ),
        Appointment(
            id = "A004",
            patientId = "P004",
            doctorId = "D002",
            date = "2025-11-15",
            time = "11:00",
            reason = "Flu symptoms",
            status = AppointmentStatus.Completed
        ),
        Appointment(
            id = "A005",
            patientId = "P005",
            doctorId = "D004",
            date = "2025-11-23",
            time = "15:30",
            reason = "Skin condition consultation",
            status = AppointmentStatus.Upcoming
        )
    )

    /** Sample treatments for testing and demonstration purposes */
    val treatments = listOf(
        Treatment(
            id = "T001",
            appointmentId = "A003",
            patientId = "P003",
            diagnosis = "Healthy, no issues found",
            treatment = "Continue regular exercise and balanced diet. Prescribed multivitamin supplement.",
            notes = "All vital signs normal. Next checkup in 12 months.",
            date = "2025-11-18"
        ),
        Treatment(
            id = "T002",
            appointmentId = "A004",
            patientId = "P004",
            diagnosis = "Viral upper respiratory infection (Common cold)",
            treatment = "Rest, fluids, and over-the-counter decongestants. Prescribed: Paracetamol 500mg as needed.",
            notes = "Patient advised to return if symptoms worsen or persist beyond 7 days.",
            date = "2025-11-15"
        )
    )

    /** Retrieves a patient by their unique ID */
    fun getPatientById(id: String) = patients.find { it.id == id }

    /** Retrieves a doctor by their unique ID */
    fun getDoctorById(id: String) = doctors.find { it.id == id }

    /** Retrieves an appointment by its unique ID */
    fun getAppointmentById(id: String) = appointments.find { it.id == id }

    /** Retrieves all treatments for a specific patient */
    fun getTreatmentsByPatientId(patientId: String) = treatments.filter { it.patientId == patientId }

    /** Retrieves all treatments for a specific appointment */
    fun getTreatmentsByAppointmentId(appointmentId: String) = treatments.filter { it.appointmentId == appointmentId }
}
