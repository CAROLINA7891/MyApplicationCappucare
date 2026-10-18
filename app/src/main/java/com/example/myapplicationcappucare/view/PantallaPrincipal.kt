package com.example.myapplicationcappucare.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplicationcappucare.model.CategoriaInsumo
import com.example.myapplicationcappucare.model.Perfil
import com.example.myapplicationcappucare.model.Producto
import com.example.myapplicationcappucare.viewmodel.CappucareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    viewModel: CappucareViewModel,
    onCerrarSesion: () -> Unit,
    onAgregarProducto: () -> Unit,
    onProductoClick: (String) -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaInsumo?>(null) }

    val usuarioActivo by viewModel.usuarioActivo.collectAsState()
    val productosFiltrados = viewModel.buscarProductos(textoBusqueda, categoriaSeleccionada)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario Médico Cappucare") }
            )
        },
        floatingActionButton = {
            // RN-08: Solo el Personal Encargado puede agregar productos
            if (usuarioActivo?.perfil == Perfil.ENCARGADO) {
                FloatingActionButton(onClick = onAgregarProducto) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Usuario: ${usuarioActivo?.nombre ?: "Invitado"} (${usuarioActivo?.perfil ?: ""})",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // RF-08: Buscador
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar por nombre o código") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // RF-02: Filtro por Categorías
            FiltroCategorias(
                categoriaSeleccionada = categoriaSeleccionada,
                onCategoriaSeleccionada = { categoriaSeleccionada = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de productos
            if (productosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos para mostrar")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(productosFiltrados, key = { it.id }) { producto ->
                        TarjetaProducto(
                            producto = producto,
                            onClick = { onProductoClick(producto.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroCategorias(
    categoriaSeleccionada: CategoriaInsumo?,
    onCategoriaSeleccionada: (CategoriaInsumo?) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido }
    ) {
        OutlinedTextField(
            value = categoriaSeleccionada?.etiqueta ?: "Todas las categorías",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todas las categorías") },
                onClick = {
                    onCategoriaSeleccionada(null)
                    expandido = false
                }
            )
            CategoriaInsumo.entries.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.etiqueta) },
                    onClick = {
                        onCategoriaSeleccionada(cat)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
fun TarjetaProducto(
    producto: Producto,
    onClick: () -> Unit
) {
    // RN-01 / RF-09: Identifica si el producto está por debajo del nivel mínimo
    val esBajoStock = producto.cantidadDisponible < producto.nivelMinimo

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (esBajoStock) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
                if (esBajoStock) {
                    Text(
                        text = "⚠️ Stock Bajo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Código: ${producto.codigo} | Categoría: ${producto.categoria.etiqueta}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Ubicación: ${producto.ubicacion} | Estado: ${producto.estado}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Disponible: ${producto.cantidadDisponible} ${producto.unidadMedida} (Mínimo: ${producto.nivelMinimo})",
                style = MaterialTheme.typography.bodyMedium,
                color = if (esBajoStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}