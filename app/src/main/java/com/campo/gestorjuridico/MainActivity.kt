package com.example.gestorjuridico

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestorjuridico.ui.components.BottomNavBar
import com.example.gestorjuridico.ui.screens.CedulaLoginScreen
import com.example.gestorjuridico.ui.screens.HomeScreen
import com.example.gestorjuridico.ui.screens.ProcessDetailScreen
import com.example.gestorjuridico.ui.screens.ProcessesScreen
import com.example.gestorjuridico.ui.theme.GestorJuridicoTheme
import com.example.gestorjuridico.viewmodel.LegalViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestorJuridicoTheme {
                GestorJuridicoApp()
            }
        }
    }
}

@Composable
fun GestorJuridicoApp(
    legalViewModel: LegalViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("gestor_juridico_prefs", Context.MODE_PRIVATE)
    }

    val savedCedula = remember { prefs.getString("locked_cedula", null) }
    val initialDestination = if (savedCedula != null) "home" else "login"

    LaunchedEffect(savedCedula) {
        if (savedCedula != null) {
            legalViewModel.loginWithCedula(savedCedula) {}
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: initialDestination

    val dashboardState by legalViewModel.dashboardState.collectAsState()
    val processesState by legalViewModel.processesState.collectAsState()
    val selectedProcessState by legalViewModel.selectedProcessState.collectAsState()
    val selectedCategory by legalViewModel.selectedCategory.collectAsState()
    val searchQuery by legalViewModel.searchQuery.collectAsState()

    Scaffold(
        bottomBar = {
            if (currentRoute != "login") {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = initialDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                CedulaLoginScreen(
                    onLoginSuccess = { confirmedCedula ->
                        prefs.edit().putString("locked_cedula", confirmedCedula).apply()

                        legalViewModel.loginWithCedula(confirmedCedula) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    dashboardState = dashboardState,
                    onNavigateToProcesses = {
                        legalViewModel.updateSearchQuery("")
                        navController.navigate("processes")
                    },
                    onSearchToProcesses = { query ->
                        legalViewModel.selectCategory("Todos")
                        legalViewModel.updateSearchQuery(query)
                        navController.navigate("processes") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onProcessClick = { processId, tab ->
                        navController.navigate("detail/$processId?tab=$tab")
                    },
                    onRadicarProceso = { radicado ->
                        legalViewModel.vincularPorRadicado(radicado)
                    }
                )
            }

            composable("processes") {
                ProcessesScreen(
                    processesState = processesState,
                    selectedCategory = selectedCategory,
                    searchQuery = searchQuery,
                    onSelectCategory = legalViewModel::selectCategory,
                    onSearchQueryChange = legalViewModel::updateSearchQuery,
                    onProcessClick = { processId, tab ->
                        navController.navigate("detail/$processId?tab=$tab")
                    },
                    onRadicarProceso = { radicado ->
                        legalViewModel.vincularPorRadicado(radicado) {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    },
                    onAddProcessToHome = { processId ->
                        legalViewModel.agregarProcesoAInicio(processId)
                    }
                )
            }

            composable(
                route = "detail/{processId}?tab={tab}",
                arguments = listOf(
                    navArgument("processId") { type = NavType.StringType },
                    navArgument("tab") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { backStackEntry ->
                val processId = backStackEntry.arguments?.getString("processId") ?: "proc_laboral_3"
                val initialTab = backStackEntry.arguments?.getInt("tab") ?: 0

                LaunchedEffect(processId) {
                    legalViewModel.loadProcessDetail(processId)
                }

                ProcessDetailScreen(
                    processState = selectedProcessState,
                    initialTab = initialTab,
                    onBackClick = { navController.popBackStack() },
                    onAddProcessToHome = { id ->
                        legalViewModel.agregarProcesoAInicio(id)
                    }
                )
            }
        }
    }
}