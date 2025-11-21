package com.example.cliniccare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cliniccare.ui.theme.*
import com.example.cliniccare.viewmodel.AppViewModelProvider
import com.example.cliniccare.viewmodel.ClinicViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDoctorScreen(
    navController: NavController,
    doctorId: String? = null,
    viewModel: ClinicViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val isEdit = doctorId != null

    val existingDoctor = if (isEdit && doctorId != null) {
        viewModel.getDoctorById(doctorId).collectAsState(initial = null).value
    } else null

    var name by remember { mutableStateOf(existingDoctor?.name ?: "") }
    var specialty by remember { mutableStateOf(existingDoctor?.specialty ?: "") }
    var phone by remember { mutableStateOf(existingDoctor?.phone ?: "") }
    var email by remember { mutableStateOf(existingDoctor?.email ?: "") }

    LaunchedEffect(existingDoctor) {
        existingDoctor?.let {
            name = it.name
            specialty = it.specialty
            phone = it.phone
            email = it.email
        }
    }

    var nameError by remember { mutableStateOf(false) }
    var specialtyError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val toastMessage by viewModel.toastMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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

    fun validateAndSubmit() {
        if (isEdit && doctorId != null) {
            viewModel.updateDoctor(
                id = doctorId,
                name = name,
                specialty = specialty,
                phone = phone,
                email = email
            )
        } else {
            viewModel.addDoctor(
                name = name,
                specialty = specialty,
                phone = phone,
                email = email
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Doctor" else "Add Doctor", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = false },
                        label = { Text("Full Name *") },
                        isError = nameError,
                        supportingText = { if (nameError) Text("Name is required") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )

                    OutlinedTextField(
                        value = specialty,
                        onValueChange = { specialty = it; specialtyError = false },
                        label = { Text("Specialty *") },
                        isError = specialtyError,
                        supportingText = { if (specialtyError) Text("Specialty is required") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it; phoneError = false },
                        label = { Text("Phone Number *") },
                        isError = phoneError,
                        supportingText = { if (phoneError) Text("Phone is required") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailError = false },
                        label = { Text("Email *") },
                        isError = emailError,
                        supportingText = { if (emailError) Text("Email is required") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary
                        )
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
                    onClick = { validateAndSubmit() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(if (isEdit) "Update" else "Save") }
            }
        }
    }
}
