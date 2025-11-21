package com.example.cliniccare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cliniccare.data.*
import com.example.cliniccare.navigation.Screen
import com.example.cliniccare.ui.theme.*
import com.example.cliniccare.viewmodel.AppViewModelProvider
import com.example.cliniccare.viewmodel.ClinicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentListScreen(
    navController: NavController,
    viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val allAppointments by viewModel.appointmentList.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                TopAppBar(
                    title = { Text("Appointments", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )

                // Custom Tab Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Background, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .background(
                                    if (selectedTab == index) Color.White else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selectedTab == index) TextDark else Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddAppointment.route) },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Add Appointment")
            }
        }
    ) { paddingValues ->
        val filteredAppointments = if (selectedTab == 0) {
            allAppointments.filter { it.status == AppointmentStatus.Upcoming }
        } else {
            allAppointments.filter { it.status == AppointmentStatus.Completed || it.status == AppointmentStatus.Cancelled }
        }

        if (filteredAppointments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Background,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No ${tabs[selectedTab].lowercase()} appointments",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = if (selectedTab == 0) "Tap + to schedule an appointment" else "No past appointments yet",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredAppointments) { appointment ->
                    AppointmentListItem(
                        appointment = appointment,
                        onClick = { navController.navigate(Screen.AppointmentDetail.createRoute(appointment.id)) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun AppointmentListItem(
    appointment: com.example.cliniccare.data.Appointment,
    onClick: () -> Unit,
    viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val patient by viewModel.getPatientById(appointment.patientId).collectAsState(initial = null)
    val doctor by viewModel.getDoctorById(appointment.doctorId).collectAsState(initial = null)
    val statusColor = when (appointment.status) {
        AppointmentStatus.Upcoming -> StatusUpcoming
        AppointmentStatus.Completed -> StatusCompleted
        AppointmentStatus.Cancelled -> StatusCancelled
    }

    // Parse date for badge
    val dateParts = appointment.date.split("-")
    val day = if (dateParts.size >= 3) dateParts[2] else "01"
    val month = if (dateParts.size >= 2) {
        java.time.Month.of(dateParts[1].toIntOrNull() ?: 1).name.take(3)
    } else "JAN"

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Secondary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = day,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Secondary
                    )
                    Text(
                        text = month,
                        fontSize = 10.sp,
                        color = Secondary
                    )
                }
            }

            // Appointment Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = appointment.time,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = appointment.status.name,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = patient?.name ?: "Unknown",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = doctor?.name ?: "Unknown",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = appointment.reason,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

