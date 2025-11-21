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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cliniccare.data.Gender
import com.example.cliniccare.ui.theme.*
import com.example.cliniccare.viewmodel.AppViewModelProvider
import com.example.cliniccare.viewmodel.ClinicViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPatientScreen(
    navController: NavController,
    patientId: String? = null,
    viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val isEdit = patientId != null

    // Load existing patient if editing
    val existingPatient = if (isEdit && patientId != null) {
        viewModel.getPatientById(patientId).collectAsState(initial = null).value
    } else null

    var name by remember { mutableStateOf(existingPatient?.name ?: "") }
    var gender by remember { mutableStateOf(existingPatient?.gender ?: Gender.Female) }
    var dateOfBirth by remember { mutableStateOf(existingPatient?.dateOfBirth ?: "") }
    var phone by remember { mutableStateOf(existingPatient?.phone ?: "") }
    var address by remember { mutableStateOf(existingPatient?.address ?: "") }

    // Update state when existingPatient loads
    LaunchedEffect(existingPatient) {
        existingPatient?.let {
            name = it.name
            gender = it.gender
            dateOfBirth = it.dateOfBirth
            phone = it.phone
            address = it.address
        }
    }

    var nameError by remember { mutableStateOf(false) }
    var dateOfBirthError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }

    var showGenderMenu by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe toast messages
    val toastMessage by viewModel.toastMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            if (message.startsWith("Success:")) {
                navController.navigateUp()
            }
            viewModel.clearToast()
        }
    }

    fun validateAndSubmit() {
        val calculatedAge = if (dateOfBirth.isNotBlank()) {
            viewModel.calculateAge(dateOfBirth)
        } else 0

        if (isEdit && patientId != null) {
            viewModel.updatePatient(
                id = patientId,
                name = name,
                age = calculatedAge,
                gender = gender,
                dateOfBirth = dateOfBirth,
                phone = phone,
                address = address
            )
        } else {
            viewModel.addPatient(
                name = name,
                age = calculatedAge,
                gender = gender,
                dateOfBirth = dateOfBirth,
                phone = phone,
                address = address
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEdit) "Edit Patient" else "Add Patient",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Full Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        label = { Text("Full Name *") },
                        placeholder = { Text("Enter full name") },
                        isError = nameError,
                        supportingText = {
                            if (nameError) Text("Name is required")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )

                    // Gender
                    ExposedDropdownMenuBox(
                        expanded = showGenderMenu,
                        onExpandedChange = { showGenderMenu = it }
                    ) {
                        OutlinedTextField(
                            value = gender.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGenderMenu)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                focusedLabelColor = Primary
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = showGenderMenu,
                            onDismissRequest = { showGenderMenu = false }
                        ) {
                            Gender.values().forEach { genderOption ->
                                DropdownMenuItem(
                                    text = { Text(genderOption.name) },
                                    onClick = {
                                        gender = genderOption
                                        showGenderMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Date of Birth with DatePicker
                    var showDatePicker by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Date of Birth *") },
                        placeholder = { Text("Select date") },
                        isError = dateOfBirthError,
                        supportingText = {
                            if (dateOfBirthError) Text("Date of birth is required")
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, "Select date", tint = Primary)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDateSelected = { selectedDate ->
                                dateOfBirth = selectedDate
                                dateOfBirthError = false
                                showDatePicker = false
                            },
                            onDismiss = { showDatePicker = false }
                        )
                    }

                    // Phone
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            phoneError = false
                        },
                        label = { Text("Phone Number *") },
                        placeholder = { Text("Enter phone number") },
                        isError = phoneError,
                        supportingText = {
                            if (phoneError) Text("Phone number is required")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )

                    // Address
                    OutlinedTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            addressError = false
                        },
                        label = { Text("Address *") },
                        placeholder = { Text("Enter address") },
                        isError = addressError,
                        supportingText = {
                            if (addressError) Text("Address is required")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { validateAndSubmit() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isEdit) "Update" else "Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
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
                        val selectedDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
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

