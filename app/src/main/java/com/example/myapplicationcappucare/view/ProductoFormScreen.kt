package com.example.myapplicationcappucare.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapplicationcappucare.model.CategoriaInsumo
import com.example.myapplicationcappucare.model.Producto
import com.example.myapplicationcappucare.viewmodel.CappucareViewModel
import com.example.myapplicationcappucare.viewmodel.ErroresProducto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    viewModel: CappucareViewModel,
    productoExistente: Producto? = null,
    onGuardarExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    var codigo by remember { mutableStateOf(productoExistente?.codigo ?: "") }
    var nombre by remember { mutableStateOf(productoExistente?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(productoExistente?.descripcion ?: "") }
    var categoria by remember { mutableStateOf<CategoriaInsumo?>(productoExistente?.categoria) }
    var unidadMedida by remember { mutableStateOf(productoExistente?.unidadMedida ?: "") }
    var nivelMinimoStr by remember { mutableStateOf(productoExistente?.nivelMinimo?.toString() ?: "") }
    var ubicacion by remember { mutableStateOf(productoExistente?.ubicacion ?: "") }

    var errores by remember { mutableStateOf(ErroresProducto()) }
    var expandidoCategoria by remember { mutableStateOf(false) }

    // Estado para la foto capturada con el recurso nativo Cámara
    var fotoProducto by remember { mutableStateOf<Bitmap?>(null) }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoProducto = bitmap
        }
    }

    val launcherPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            launcherCamara.launch(null)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (productoExistente == null) "Nuevo Producto" else "Editar Producto")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sección Recurso Nativo: Cámara del dispositivo
            OutlinedButton(
                onClick = {
                    val permisoOtorgado = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (permisoOtorgado) {
                        launcherCamara.launch(null)
                    } else {
                        launcherPermisoCamara.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (fotoProducto == null) "📷 Tomar foto del producto" else "📷 Volver a tomar foto")
            }

            AnimatedVisibility(
                visible = fotoProducto != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                fotoProducto?.let { bmp ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Foto del producto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }

            // RF-01 & RN-04: Código de producto (Único)
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código *") },
                isError = errores.codigo != null,
                supportingText = { errores.codigo?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Producto *") },
                isError = errores.nombre != null,
                supportingText = { errores.nombre?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            // RF-02: Selector de Categoría
            ExposedDropdownMenuBox(
                expanded = expandidoCategoria,
                onExpandedChange = { expandidoCategoria = !expandidoCategoria }
            ) {
                OutlinedTextField(
                    value = categoria?.etiqueta ?: "Seleccionar Categoría *",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    isError = errores.categoria != null,
                    supportingText = { errores.categoria?.let { Text(it) } },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCategoria) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandidoCategoria,
                    onDismissRequest = { expandidoCategoria = false }
                ) {
                    CategoriaInsumo.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.etiqueta) },
                            onClick = {
                                categoria = cat
                                expandidoCategoria = false
                            }
                        )
                    }
                }
            }

            // Unidad de Medida
            OutlinedTextField(
                value = unidadMedida,
                onValueChange = { unidadMedida = it },
                label = { Text("Unidad de Medida (ej. cajas, frascos) *") },
                isError = errores.unidadMedida != null,
                supportingText = { errores.unidadMedida?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // Nivel Mínimo
            OutlinedTextField(
                value = nivelMinimoStr,
                onValueChange = { nivelMinimoStr = it.filter { char -> char.isDigit() } },
                label = { Text("Nivel Mínimo de Existencias *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errores.nivelMinimo != null,
                supportingText = { errores.nivelMinimo?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // Ubicación
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación en Almacén *") },
                isError = errores.ubicacion != null,
                supportingText = { errores.ubicacion?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botones Guardar y Cancelar
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onVolver,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val validacion = viewModel.validarProducto(
                            idActual = productoExistente?.id,
                            codigo = codigo,
                            nombre = nombre,
                            categoria = categoria,
                            unidadMedida = unidadMedida,
                            nivelMinimoStr = nivelMinimoStr,
                            ubicacion = ubicacion
                        )
                        errores = validacion

                        if (!validacion.tieneErrores && categoria != null) {
                            val min = nivelMinimoStr.toIntOrNull() ?: 0
                            val productoGuardar = Producto(
                                id = productoExistente?.id ?: "",
                                codigo = codigo.trim(),
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim(),
                                categoria = categoria!!,
                                unidadMedida = unidadMedida.trim(),
                                cantidadDisponible = productoExistente?.cantidadDisponible ?: 0,
                                nivelMinimo = min,
                                ubicacion = ubicacion.trim()
                            )
                            viewModel.guardarProducto(productoGuardar)
                            onGuardarExitoso()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}