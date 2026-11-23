package com.example.myapplicationcappucare.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationcappucare.data.MockData
import com.example.myapplicationcappucare.viewmodel.CappucareViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    viewModel: CappucareViewModel,
    onLoginExitoso: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }
    var errorUsuario by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    // Paleta de colores Cappucare según tu diseño
    val turquesaSuperior = Color(0xFF3FD5D2)
    val turquesaInferior = Color(0xFFA6F2EC)
    val azulMarino = Color(0xFF0A3977)

    val autocompletarDemo = {
        MockData.usuarios.firstOrNull()?.let { demo ->
            usuario = demo.usuario
            clave = demo.clave
            errorUsuario = null
            errorClave = null
            viewModel.limpiarErrorLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(turquesaSuperior, turquesaInferior)
                )
            )
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Círculo azul marino con corazón blanco (doble clic autocompleta para demo)
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(azulMarino)
                    .combinedClickable(
                        onClick = {},
                        onDoubleClick = { autocompletarDemo() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Logo Cappucare",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cappucare",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = azulMarino,
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onDoubleClick = { autocompletarDemo() }
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Acceso Cappucare",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = azulMarino
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Campo Usuario / Correo estilo píldora blanca
            TextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    errorUsuario = null
                    viewModel.limpiarErrorLogin()
                },
                placeholder = { Text("Usuario / Correo", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Usuario",
                        tint = azulMarino
                    )
                },
                isError = errorUsuario != null,
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(50))
            )

            AnimatedVisibility(visible = errorUsuario != null) {
                Text(
                    text = errorUsuario ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Campo Contraseña estilo píldora blanca con botón "Ver"
            TextField(
                value = clave,
                onValueChange = {
                    clave = it
                    errorClave = null
                    viewModel.limpiarErrorLogin()
                },
                placeholder = { Text("Contraseña", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Contraseña",
                        tint = azulMarino
                    )
                },
                trailingIcon = {
                    Text(
                        text = if (claveVisible) "Ocultar" else "Ver",
                        color = azulMarino,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { claveVisible = !claveVisible }
                    )
                },
                visualTransformation = if (claveVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = errorClave != null,
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(50))
            )

            AnimatedVisibility(visible = errorClave != null) {
                Text(
                    text = errorClave ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            // Mensaje de error de credenciales animadas
            AnimatedVisibility(
                visible = viewModel.errorLogin != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = viewModel.errorLogin ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Ingresar azul marino redondeado
            Button(
                onClick = {
                    errorUsuario = if (usuario.isBlank()) "Ingresa tu usuario" else null
                    errorClave = if (clave.isBlank()) "Ingresa tu contraseña" else null

                    if (errorUsuario == null && errorClave == null) {
                        if (viewModel.login(usuario, clave)) {
                            onLoginExitoso()
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = azulMarino,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(6.dp, RoundedCornerShape(50))
            ) {
                Text(
                    text = "Ingresar",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = azulMarino,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Regístrate",
                color = azulMarino,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}