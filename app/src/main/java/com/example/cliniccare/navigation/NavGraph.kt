package com.example.cliniccare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cliniccare.ui.screens.*

/** Sets up the navigation graph for the app */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        /** Defines the splash screen route */
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        /** Defines the dashboard screen route */
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        /** Defines the patient list screen route */
        composable(Screen.PatientList.route) {
            PatientListScreen(navController = navController)
        }

        /** Defines the patient detail screen route with patientId parameter */
        composable(
            route = Screen.PatientDetail.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            PatientDetailScreen(navController = navController, patientId = patientId)
        }

        /** Defines the add patient screen route */
        composable(Screen.AddPatient.route) {
            AddEditPatientScreen(navController = navController)
        }

        /** Defines the edit patient screen route with patientId parameter */
        composable(
            route = Screen.EditPatient.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            AddEditPatientScreen(navController = navController, patientId = patientId)
        }

        /** Defines the doctor list screen route */
        composable(Screen.DoctorList.route) {
            DoctorListScreen(navController = navController)
        }

        /** Defines the doctor detail screen route with doctorId parameter */
        composable(
            route = Screen.DoctorDetail.route,
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            DoctorDetailScreen(navController = navController, doctorId = doctorId)
        }

        /** Defines the add doctor screen route */
        composable(Screen.AddDoctor.route) {
            AddEditDoctorScreen(navController = navController)
        }

        /** Defines the edit doctor screen route with doctorId parameter */
        composable(
            route = Screen.EditDoctor.route,
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            AddEditDoctorScreen(navController = navController, doctorId = doctorId)
        }

        /** Defines the appointment list screen route */
        composable(Screen.AppointmentList.route) {
            AppointmentListScreen(navController = navController)
        }

        /** Defines the appointment detail screen route with appointmentId parameter */
        composable(
            route = Screen.AppointmentDetail.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            AppointmentDetailScreen(navController = navController, appointmentId = appointmentId)
        }

        /** Defines the add appointment screen route */
        composable(Screen.AddAppointment.route) {
            AddEditAppointmentScreen(navController = navController)
        }

        /** Defines the edit appointment screen route with appointmentId parameter */
        composable(
            route = Screen.EditAppointment.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            AddEditAppointmentScreen(navController = navController, appointmentId = appointmentId)
        }

        /** Defines the treatment history screen route */
        composable(Screen.TreatmentHistory.route) {
            TreatmentHistoryScreen(navController = navController)
        }

        /** Defines the add treatment screen route with optional appointmentId parameter */
        composable(
            route = Screen.AddTreatment.route,
            arguments = listOf(navArgument("appointmentId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")
            AddTreatmentScreen(navController = navController, appointmentId = appointmentId)
        }
    }
}
