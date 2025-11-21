package com.example.cliniccare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cliniccare.navigation.Screen
import com.example.cliniccare.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cliniccare.viewmodel.AppViewModelProvider
import com.example.cliniccare.viewmodel.ClinicViewModel
import com.example.cliniccare.data.* // ensure AppointmentStatus etc.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(navController: NavController, doctorId: String, viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val doctor by viewModel.getDoctorById(doctorId).collectAsState(initial = null)
    val allAppointments by viewModel.appointmentList.collectAsState()
    val upcomingAppointmentsList = allAppointments.filter { it.doctorId == doctorId && it.status == AppointmentStatus.Upcoming }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val toastMessage by viewModel.toastMessage.collectAsState()
    LaunchedEffect(toastMessage) {
        if (toastMessage != null && toastMessage!!.startsWith("Success: Doctor Deleted")) {
            navController.navigateUp()
            viewModel.clearToast()
        }
    }

    if (doctor == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Doctor not found")
        }
        return
    }

    val d = doctor!! // Safe because we returned if null
    Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Doctor Details", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.EditDoctor.createRoute(doctorId)) }) {
                            Icon(Icons.Default.Edit, "Edit", tint = Primary)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete", tint = ErrorRed)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Basic Info Card with Icon
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PrimaryLight,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = d.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PrimaryLight
                                ) {
                                    Text(
                                        text = d.specialty,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Contact Info
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Phone Number",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = d.phone,
                                    fontSize = 15.sp,
                                    color = TextDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Email",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = d.email,
                                    fontSize = 15.sp,
                                    color = TextDark
                                )
                            }
                        }
                    }
                }

                // Upcoming Appointments Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Upcoming Appointments",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (upcomingAppointmentsList.isEmpty()) {
                            Text(
                                text = "No upcoming appointments",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            upcomingAppointmentsList.forEach { appt ->
                                val patientState = viewModel.getPatientById(appt.patientId).collectAsState(initial = null)
                                val apptPatient = patientState.value
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Background
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = SecondaryLight,
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.CalendarToday,
                                                    contentDescription = null,
                                                    tint = Secondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = apptPatient?.name ?: "Unknown",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextDark
                                            )
                                            Text(
                                                text = "${appt.date} at ${appt.time}",
                                                fontSize = 13.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = appt.reason,
                                                fontSize = 13.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Doctor") },
            text = { Text("Are you sure you want to delete this doctor? This will remove related appointments.") },
            confirmButton = { TextButton(onClick = { doctor?.let { viewModel.deleteDoctor(it) }; showDeleteDialog = false }) { Text("Delete", color = ErrorRed) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }
}
