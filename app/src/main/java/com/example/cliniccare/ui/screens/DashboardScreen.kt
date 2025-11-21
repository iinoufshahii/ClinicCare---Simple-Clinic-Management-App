package com.example.cliniccare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cliniccare.navigation.Screen
import com.example.cliniccare.ui.theme.*
import com.example.cliniccare.viewmodel.AppViewModelProvider
import com.example.cliniccare.viewmodel.ClinicViewModel

@OptIn(ExperimentalMaterial3Api::class)
/** Main dashboard screen displaying key statistics and navigation options */
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val totalPatients by viewModel.totalPatients.collectAsState()
    val totalDoctors by viewModel.totalDoctors.collectAsState()
    val upcomingAppointments by viewModel.upcomingAppointmentsCount.collectAsState()
    val treatmentCount by viewModel.treatmentCount.collectAsState()
    val todayAppointments by viewModel.todayAppointmentsCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Dashboard",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Dashboard Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(
                    title = "Patients",
                    count = totalPatients,
                    subtitle = "$totalPatients registered patients",
                    icon = Icons.Default.Person,
                    backgroundColor = Primary,
                    lightBackgroundColor = PrimaryLight,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.PatientList.route) }
                )

                DashboardCard(
                    title = "Doctors",
                    count = totalDoctors,
                    subtitle = "$totalDoctors medical professionals",
                    icon = Icons.Default.MedicalServices,
                    backgroundColor = Primary,
                    lightBackgroundColor = PrimaryLight,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.DoctorList.route) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(
                    title = "Appointments",
                    count = upcomingAppointments,
                    subtitle = "$upcomingAppointments upcoming visits",
                    icon = Icons.Default.CalendarToday,
                    backgroundColor = Secondary,
                    lightBackgroundColor = SecondaryLight,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.AppointmentList.route) }
                )

                DashboardCard(
                    title = "Treatment History",
                    count = treatmentCount,
                    subtitle = "$treatmentCount treatment records",
                    icon = Icons.Default.Description,
                    backgroundColor = Secondary,
                    lightBackgroundColor = SecondaryLight,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.TreatmentHistory.route) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Primary, Secondary)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Today's Overview",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Appointments Today",
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = todayAppointments.toString(),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Available Doctors",
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = totalDoctors.toString(),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
/** Composable for displaying dashboard statistics cards */
fun DashboardCard(
    title: String,
    count: Int,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    lightBackgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(lightBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = backgroundColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
