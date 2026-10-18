package com.example.myapplicationcappucare.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplicationcappucare.view.LoginScreen
import com.example.myapplicationcappucare.view.MovimientoFormScreen
import com.example.myapplicationcappucare.view.PantallaPrincipal
import com.example.myapplicationcappucare.view.ProductoFormScreen
import com.example.myapplicationcappucare.viewmodel.CappucareViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: CappucareViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // 1. Pantalla de Login (RF-00)
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginExitoso = {
                    navController.navigate("principal") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 2. Pantalla Principal / Inventario (RF-01, RF-02, RF-08)
        composable("principal") {
            PantallaPrincipal(
                viewModel = viewModel,
                onCerrarSesion = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo("principal") { inclusive = true }
                    }
                },
                onAgregarProducto = {
                    navController.navigate("producto_form")
                },
                onProductoClick = { productoId ->
                    // Navega al formulario de movimiento pasando el ID del producto
                    navController.navigate("movimiento_form/$productoId")
                }
            )
        }

        // 3. Formulario para Agregar / Editar Producto (RN-08)
        composable("producto_form") {
            ProductoFormScreen(
                viewModel = viewModel,
                onGuardarExitoso = {
                    navController.popBackStack()
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // 4. Formulario de Movimientos de Inventario (RF-03, RF-04)
        composable("movimiento_form/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            MovimientoFormScreen(
                viewModel = viewModel,
                productoId = productoId,
                onGuardarExitoso = {
                    navController.popBackStack()
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}