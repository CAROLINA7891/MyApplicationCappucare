package com.example.myapplicationcappucare.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplicationcappucare.model.MotivoSalida
import com.example.myapplicationcappucare.model.TipoMovimiento
import com.example.myapplicationcappucare.viewmodel.CappucareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoFormScreen(
    viewModel: CappucareViewModel,
    productoId: String,
    onGuardarExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    var cantidadText by remember { mutableStateOf("") }
    var tipoMovimiento by remember { mutableStateOf(TipoMovimiento.INGRESO) }
    var motivoSalida by remember { mutableStateOf<MotivoSalida?>(null) }
    var observaciones by remember { mutableStateOf("") }
    var expandidoMotivo by remember { mutableStateOf(false) }

    val producto = viewModel.productos.collectAsState().value.find { it.id == productoId }
    val errores = viewModel.validarMovimiento(
        productoId = productoId,
        tipo = tipoMovimiento,
        cantidadStr = cantidadText,
        motivo = motivoSalida
    )
    var mostrarErrores by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registrar Movimiento") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Producto: ${producto?.nombre ?: "Desconocido"}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Stock Actual: ${producto?.cantidadDisponible ?: 0} ${producto?.unidadMedida ?: ""}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selección de Tipo de Movimiento
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = tipoMovimiento == TipoMovimiento.INGRESO,
                    onClick = {
                        tipoMovimiento = TipoMovimiento.INGRESO
                        motivoSalida = null
                    },
                    label = { Text("Ingreso (+)") }
                )
                FilterChip(
                    selected = tipoMovimiento == TipoMovimiento.SALIDA,
                    onClick = { tipoMovimiento = TipoMovimiento.SALIDA },
                    label = { Text("Salida (-)") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val errorCantidad = errores.cantidad
            OutlinedTextField(
                value = cantidadText,
                onValueChange = { cantidadText = it },
                label = { Text("Cantidad") },
                isError = mostrarErrores && errorCantidad != null,
                supportingText = {
                    if (mostrarErrores && errorCantidad != null) {
                        Text(text = errorCantidad, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (tipoMovimiento == TipoMovimiento.SALIDA) {
                Spacer(modifier = Modifier.height(8.dp))
                val errorMotivo = errores.motivo
                ExposedDropdownMenuBox(
                    expanded = expandidoMotivo,
                    onExpandedChange = { expandidoMotivo = !expandidoMotivo }
                ) {
                    OutlinedTextField(
                        value = motivoSalida?.name ?: "Selecciona Motivo",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Motivo de Salida *") },
                        isError = mostrarErrores && errorMotivo != null,
                        supportingText = {
                            if (mostrarErrores && errorMotivo != null) {
                                Text(text = errorMotivo, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoMotivo) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandidoMotivo,
                        onDismissRequest = { expandidoMotivo = false }
                    ) {
                        MotivoSalida.entries.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m.name) },
                                onClick = {
                                    motivoSalida = m
                                    expandidoMotivo = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones / Motivo libre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f)) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        mostrarErrores = true
                        if (!errores.tieneErrores) {
                            val cantidad = cantidadText.toIntOrNull() ?: 0
                            viewModel.registrarMovimiento(
                                productoId = productoId,
                                tipo = tipoMovimiento,
                                cantidad = cantidad,
                                motivo = motivoSalida,
                                observacion = observaciones
                            )
                            onGuardarExitoso()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Registrar")
                }
            }
        }
    }
}