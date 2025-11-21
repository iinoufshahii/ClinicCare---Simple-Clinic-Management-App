package com.example.cliniccare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cliniccare.data.*
import com.example.cliniccare.navigation.Screen
import com.example.cliniccare.ui.theme.*
import com.example.cliniccare.viewmodel.AppViewModelProvider
import com.example.cliniccare.viewmodel.ClinicViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// Appointment Detail Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(navController: NavController, appointmentId: String, viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val appointment by viewModel.getAppointmentById(appointmentId).collectAsState(initial = null)
    val patient by viewModel.getPatientById(appointment?.patientId ?: "0").collectAsState(initial = null)
    val doctor by viewModel.getDoctorById(appointment?.doctorId ?: "0").collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Details", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (appointment == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Appointment not found")
            }
            return@Scaffold
        }

        val appt = appointment!! // safe non-null
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Appointment Information", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow("Appointment ID", appt.id)
                    DetailRow("Patient", patient?.name ?: "Unknown")
                    DetailRow("Doctor", doctor?.name ?: "Unknown")
                    DetailRow("Date", appt.date)
                    DetailRow("Time", appt.time)
                    DetailRow("Reason", appt.reason)
                    DetailRow("Status", appt.status.name, isLast = true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Screen.AddTreatment.createRoute(appt.id)) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Treatment")
            }
        }
    }
}

// Add/Edit Appointment Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAppointmentScreen(
    navController: NavController,
    appointmentId: String? = null,
    viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val isEdit = appointmentId != null
    var reason by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var selectedPatientId by remember { mutableStateOf("") }
    var selectedDoctorId by remember { mutableStateOf("") }
    val patientList by viewModel.patientList.collectAsState()
    val doctorList by viewModel.doctorList.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val toastMessage by viewModel.toastMessage.collectAsState()
    val scope = rememberCoroutineScope()

    val existingAppointment = if (isEdit && appointmentId != null) {
        viewModel.getAppointmentById(appointmentId).collectAsState(initial = null).value
    } else null

    LaunchedEffect(existingAppointment) {
        existingAppointment?.let {
            reason = it.reason
            date = it.date
            time = it.time
            selectedPatientId = it.patientId
            selectedDoctorId = it.doctorId
        }
    }

    var expandedPatient by remember { mutableStateOf(false) }
    var expandedDoctor by remember { mutableStateOf(false) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                if (message.startsWith("Success:")) {
                    navController.navigateUp()
                }
                viewModel.clearToast()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Appointment" else "Add Appointment", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Patient selection
                    ExposedDropdownMenuBox(
                        expanded = expandedPatient,
                        onExpandedChange = { expandedPatient = it }
                    ) {
                        OutlinedTextField(
                            value = patientList.find { it.id == selectedPatientId }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Patient *") },
                            placeholder = { Text("Select patient") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPatient,
                            onDismissRequest = { expandedPatient = false }
                        ) {
                            patientList.forEach { patient ->
                                DropdownMenuItem(
                                    text = { Text(patient.name) },
                                    onClick = {
                                        selectedPatientId = patient.id
                                        expandedPatient = false
                                    }
                                )
                            }
                        }
                    }
                    // Doctor selection
                    ExposedDropdownMenuBox(
                        expanded = expandedDoctor,
                        onExpandedChange = { expandedDoctor = it }
                    ) {
                        OutlinedTextField(
                            value = doctorList.find { it.id == selectedDoctorId }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Doctor *") },
                            placeholder = { Text("Select doctor") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDoctor,
                            onDismissRequest = { expandedDoctor = false }
                        ) {
                            doctorList.forEach { doctor ->
                                DropdownMenuItem(
                                    text = { Text(doctor.name) },
                                    onClick = {
                                        selectedDoctorId = doctor.id
                                        expandedDoctor = false
                                    }
                                )
                            }
                        }
                    }
                    // Date picker for appointment
                    var showDatePicker by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = date,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Date *") },
                        placeholder = { Text("Select date") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, "Select date", tint = Primary)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )

                    if (showDatePicker) {
                        AppointmentDatePickerDialog(
                            onDateSelected = { selectedDate ->
                                date = selectedDate
                                showDatePicker = false
                            },
                            onDismiss = { showDatePicker = false }
                        )
                    }
                    // Time picker for appointment
                    var showTimePicker by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = time,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Time *") },
                        placeholder = { Text("Select time") },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(Icons.Default.Schedule, "Select time", tint = Primary)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePicker = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )

                    if (showTimePicker) {
                        TimePickerDialog(
                            onTimeSelected = { selectedTime ->
                                time = selectedTime
                                showTimePicker = false
                            },
                            onDismiss = { showTimePicker = false }
                        )
                    }
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Reason *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancel") }
                Button(
                    onClick = {
                        if (isEdit && appointmentId != null) {
                            viewModel.updateAppointment(
                                id = appointmentId,
                                patientId = selectedPatientId,
                                doctorId = selectedDoctorId,
                                date = date,
                                time = time,
                                reason = reason,
                                status = AppointmentStatus.Upcoming
                            )
                        } else {
                            viewModel.addAppointment(
                                patientId = selectedPatientId,
                                doctorId = selectedDoctorId,
                                date = date,
                                time = time,
                                reason = reason
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(if (isEdit) "Update" else "Save") }
            }
        }
    }
}

// Treatment History Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentHistoryScreen(navController: NavController, viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    var selectedPatientId by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val allTreatments by viewModel.treatmentList.collectAsState()
    val allPatients by viewModel.patientList.collectAsState()
    val filteredTreatments = if (selectedPatientId.isEmpty()) allTreatments else allTreatments.filter { it.patientId == selectedPatientId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Treatment History", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
        ) {
            // Filter Dropdown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filter by Patient",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedPatientId.isEmpty()) "All Patients" else allPatients.find { it.id == selectedPatientId }?.name ?: "All Patients",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Patients") },
                            onClick = {
                                selectedPatientId = ""
                                expanded = false
                            }
                        )
                        allPatients.forEach { patient ->
                            DropdownMenuItem(
                                text = { Text(patient.name) },
                                onClick = {
                                    selectedPatientId = patient.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Treatment List
            if (filteredTreatments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Background,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No treatment records found", fontSize = 16.sp, color = Color.Gray)
                        Text("Treatment records will appear here", fontSize = 14.sp, color = Color.LightGray)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredTreatments.forEach { treatment ->
                        val patient = allPatients.find { it.id == treatment.patientId }
                        TreatmentCard(treatment = treatment, patient = patient)
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun TreatmentCard(treatment: Treatment, patient: Patient?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFFFF4E6),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = patient?.name ?: "Unknown",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    Text(
                        text = treatment.date,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Diagnosis",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = treatment.diagnosis,
                        fontSize = 14.sp,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Treatment",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = treatment.treatment,
                        fontSize = 14.sp,
                        color = TextDark
                    )

                    if (treatment.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Notes",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = treatment.notes,
                            fontSize = 14.sp,
                            color = TextDark
                        )
                    }
                }
            }
        }
    }
}

// Add Treatment Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTreatmentScreen(navController: NavController, appointmentId: String?, viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    var diagnosis by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val appointment by viewModel.getAppointmentById(appointmentId ?: "").collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    val toastMessage by viewModel.toastMessage.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                if (message.startsWith("Success:")) {
                    navController.navigateUp()
                }
                viewModel.clearToast()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Treatment", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date picker for treatment
                    var showDatePicker by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = date,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Date *") },
                        placeholder = { Text("Select date") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, "Select date", tint = Primary)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )

                    if (showDatePicker) {
                        AppointmentDatePickerDialog(
                            onDateSelected = { selectedDate ->
                                date = selectedDate
                                showDatePicker = false
                            },
                            onDismiss = { showDatePicker = false }
                        )
                    }
                    OutlinedTextField(
                        value = diagnosis,
                        onValueChange = { diagnosis = it },
                        label = { Text("Diagnosis *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                    OutlinedTextField(
                        value = treatment,
                        onValueChange = { treatment = it },
                        label = { Text("Treatment *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancel") }
                Button(
                    onClick = {
                        appointment?.let {
                            viewModel.addTreatment(
                                appointmentId = it.id,
                                patientId = it.patientId,
                                diagnosis = diagnosis,
                                treatment = treatment,
                                notes = notes,
                                date = date
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Save") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            .format(java.util.Date(millis))
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text("OK", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = Primary,
                todayContentColor = Primary,
                todayDateBorderColor = Primary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time", fontWeight = FontWeight.SemiBold) },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialSelectedContentColor = Color.White,
                    clockDialColor = PrimaryLight,
                    selectorColor = Primary,
                    timeSelectorSelectedContainerColor = Primary,
                    timeSelectorSelectedContentColor = Color.White
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val hour = timePickerState.hour.toString().padStart(2, '0')
                    val minute = timePickerState.minute.toString().padStart(2, '0')
                    onTimeSelected("$hour:$minute")
                }
            ) {
                Text("OK", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
