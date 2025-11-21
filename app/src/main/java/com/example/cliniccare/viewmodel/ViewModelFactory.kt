package com.example.cliniccare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cliniccare.ClinicApplication

/** Provides ViewModel factory for dependency injection */
object AppViewModelProvider {
    /** Factory for creating ClinicViewModel instances with repository injection */
    val Factory = viewModelFactory {
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ClinicApplication)
            ClinicViewModel(app.repository)
        }
    }
}
