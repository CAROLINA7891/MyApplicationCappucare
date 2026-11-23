package com.example.myapplicationcappucare.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationcappucare.model.CategoriaInsumo
import com.example.myapplicationcappucare.model.EstadoProducto
import com.example.myapplicationcappucare.model.Movimiento
import com.example.myapplicationcappucare.model.Perfil
import com.example.myapplicationcappucare.model.Producto
import com.example.myapplicationcappucare.model.TipoMovimiento
import com.example.myapplicationcappucare.viewmodel.CappucareViewModel
import kotlinx.coroutines.launch

// Paleta visual Cappucare
private val FondoMentaClaro = Color(0xFFD5ECEA)
private val FondoMentaSuave = Color(0xFFC1E3E0)
private val TarjetaGrisPizarra = Color(0xFF5A6565).copy(alpha = 0.86f)
private val TarjetaGrisAlerta = Color(0xFF6B4244).copy(alpha = 0.90f)
private val TurquesaStock = Color(0xFF5CE6DC)
private val AzulMarinoCappucare = Color(0xFF0A3977)
private val FondoDrawerOscuro = Color(0xFF0B222A)
private val ItemActivoDrawer = Color(0xFF137C82)

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
    var soloBajoMinimo by remember { mutableStateOf(false) }
    var pestanaSeleccionada by remember { mutableIntStateOf(0) } // 0 = Inventario, 1 = Historial
    var seccionDrawer by remember { mutableStateOf("INVENTARIO") } // "INICIO" o "INVENTARIO"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val usuarioActivo by viewModel.usuarioActivo.collectAsState()
    val listaProductosBaseDatos by viewModel.productos.collectAsState()
    val listaMovimientosBaseDatos by viewModel.movimientos.collectAsState()

    val productosCriticos = remember(listaProductosBaseDatos) {
        listaProductosBaseDatos.filter {
            it.estado != EstadoProducto.DADO_DE_BAJA && it.cantidadDisponible < it.nivelMinimo
        }
    }

    val productosFiltrados = remember(
        listaProductosBaseDatos,
        textoBusqueda,
        categoriaSeleccionada,
        soloBajoMinimo
    ) {
        listaProductosBaseDatos.filter { producto ->
            val query = textoBusqueda.trim()
            val coincideTexto = query.isEmpty() ||
                    producto.nombre.contains(query, ignoreCase = true) ||
                    producto.codigo.contains(query, ignoreCase = true)
            val coincideCat = categoriaSeleccionada == null || producto.categoria == categoriaSeleccionada
            val coincideAlerta = !soloBajoMinimo ||
                    (producto.estado != EstadoProducto.DADO_DE_BAJA &&
                            producto.cantidadDisponible < producto.nivelMinimo)

            coincideTexto && coincideCat && coincideAlerta
        }.sortedBy { it.nombre }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = FondoDrawerOscuro,
                drawerContentColor = Color.White,
                modifier = Modifier.width(270.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Cappucare",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TurquesaStock,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(12.dp))

                // Opción 1: Inicio
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", fontWeight = FontWeight.SemiBold) },
                    selected = seccionDrawer == "INICIO",
                    onClick = {
                        seccionDrawer = "INICIO"
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = ItemActivoDrawer,
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.8f),
                        unselectedTextColor = Color.White.copy(alpha = 0.85f)
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción 2: Inventario Médicos
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Inventario Médicos") },
                    label = { Text("Inventario Médicos", fontWeight = FontWeight.SemiBold) },
                    selected = seccionDrawer == "INVENTARIO",
                    onClick = {
                        seccionDrawer = "INVENTARIO"
                        pestanaSeleccionada = 0
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = ItemActivoDrawer,
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.8f),
                        unselectedTextColor = Color.White.copy(alpha = 0.85f)
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción 3: Ventas / Salida Insumos
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Ventas / Salida Insumos") },
                    label = { Text("Ventas / Salida Insumos", fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        val primerProducto = listaProductosBaseDatos.firstOrNull()
                        if (primerProducto != null) {
                            onProductoClick(primerProducto.id)
                        } else {
                            seccionDrawer = "INVENTARIO"
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedIconColor = Color.White.copy(alpha = 0.8f),
                        unselectedTextColor = Color.White.copy(alpha = 0.85f)
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción 4: Nuevo Producto (Solo Encargado)
                if (usuarioActivo?.perfil == Perfil.ENCARGADO) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Filled.Add, contentDescription = "Nuevo Producto") },
                        label = { Text("Nuevo Producto", fontWeight = FontWeight.SemiBold) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onAgregarProducto()
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedIconColor = Color.White.copy(alpha = 0.8f),
                            unselectedTextColor = Color.White.copy(alpha = 0.85f)
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            FondoMedicoCappucare()

            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Abrir menú lateral",
                                    tint = AzulMarinoCappucare
                                )
                            }
                        },
                        title = {
                            Text(
                                text = "Inventario Médico Cappucare",
                                color = AzulMarinoCappucare,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White.copy(alpha = 0.65f)
                        )
                    )
                },
                floatingActionButton = {
                    if (seccionDrawer == "INVENTARIO" &&
                        pestanaSeleccionada == 0 &&
                        usuarioActivo?.perfil == Perfil.ENCARGADO
                    ) {
                        FloatingActionButton(
                            onClick = onAgregarProducto,
                            containerColor = AzulMarinoCappucare,
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Usuario: ${usuarioActivo?.nombre ?: "Invitado"} (${usuarioActivo?.perfil ?: ""})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AzulMarinoCappucare,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (seccionDrawer == "INICIO") {
                        // Vista de Inicio con resumen y acceso directo al inventario
                        VistaInicioCappucare(
                            totalProductos = listaProductosBaseDatos.size,
                            totalCriticos = productosCriticos.size,
                            totalMovimientos = listaMovimientosBaseDatos.size,
                            onIrInventario = { seccionDrawer = "INVENTARIO" },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        TabRow(
                            selectedTabIndex = pestanaSeleccionada,
                            containerColor = TarjetaGrisPizarra,
                            contentColor = TurquesaStock,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Tab(
                                selected = pestanaSeleccionada == 0,
                                onClick = { pestanaSeleccionada = 0 },
                                text = {
                                    Text(
                                        text = "Inventario (${listaProductosBaseDatos.size})",
                                        color = if (pestanaSeleccionada == 0) TurquesaStock else Color.White
                                    )
                                }
                            )
                            Tab(
                                selected = pestanaSeleccionada == 1,
                                onClick = { pestanaSeleccionada = 1 },
                                text = {
                                    Text(
                                        text = "Historial (${listaMovimientosBaseDatos.size})",
                                        color = if (pestanaSeleccionada == 1) TurquesaStock else Color.White
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (pestanaSeleccionada == 0) {
                            AnimatedVisibility(
                                visible = productosCriticos.isNotEmpty(),
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                        .clickable { soloBajoMinimo = !soloBajoMinimo }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⚠️ ${productosCriticos.size} producto(s) bajo stock mínimo",
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = if (soloBajoMinimo) "Ver todos" else "Filtrar",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            TextField(
                                value = textoBusqueda,
                                onValueChange = { textoBusqueda = it },
                                placeholder = {
                                    Text(
                                        text = "Buscar por nombre o código",
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = TarjetaGrisPizarra,
                                    unfocusedContainerColor = TarjetaGrisPizarra,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = TurquesaStock
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            FiltroCategorias(
                                categoriaSeleccionada = categoriaSeleccionada,
                                onCategoriaSeleccionada = { categoriaSeleccionada = it }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (productosFiltrados.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No hay productos para mostrar",
                                        color = AzulMarinoCappucare,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        } else {
                            if (listaMovimientosBaseDatos.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Aún no hay movimientos registrados",
                                        color = AzulMarinoCappucare,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(listaMovimientosBaseDatos, key = { it.id }) { mov ->
                                        val nombreProducto = listaProductosBaseDatos
                                            .find { it.id == mov.productoId }?.nombre ?: mov.productoId
                                        TarjetaMovimiento(
                                            movimiento = mov,
                                            nombreProducto = nombreProducto
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.logout()
                            onCerrarSesion()
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AzulMarinoCappucare,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun VistaInicioCappucare(
    totalProductos: Int,
    totalCriticos: Int,
    totalMovimientos: Int,
    onIrInventario: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TarjetaGrisPizarra),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido a Cappucare",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Gestión de Inventario Médico",
                    fontSize = 14.sp,
                    color = TurquesaStock,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "• Insumos registrados: $totalProductos\n• Productos bajo mínimo: $totalCriticos\n• Movimientos registrados: $totalMovimientos",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onIrInventario,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TurquesaStock,
                        contentColor = AzulMarinoCappucare
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver Inventario Médico", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FondoMedicoCappucare() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(FondoMentaClaro, FondoMentaSuave)
                )
            )
    ) {
        val colorMarcaAgua = Color(0xFF7CB8B4).copy(alpha = 0.25f)
        val colorTrazo = Color(0xFF5D9E9A).copy(alpha = 0.22f)

        val cruzX = size.width * 0.26f
        val cruzY = size.height * 0.78f
        val grosorCruz = 48.dp.toPx()
        val largoCruz = 140.dp.toPx()
        drawRect(
            color = colorMarcaAgua,
            topLeft = Offset(cruzX - grosorCruz / 2, cruzY - largoCruz / 2),
            size = Size(grosorCruz, largoCruz)
        )
        drawRect(
            color = colorMarcaAgua,
            topLeft = Offset(cruzX - largoCruz / 2, cruzY - grosorCruz / 2),
            size = Size(largoCruz, grosorCruz)
        )

        val cruz2X = size.width * 0.74f
        val cruz2Y = size.height * 0.14f
        val grosor2 = 24.dp.toPx()
        val largo2 = 70.dp.toPx()
        drawRect(
            color = colorMarcaAgua,
            topLeft = Offset(cruz2X - grosor2 / 2, cruz2Y - largo2 / 2),
            size = Size(grosor2, largo2)
        )
        drawRect(
            color = colorMarcaAgua,
            topLeft = Offset(cruz2X - largo2 / 2, cruz2Y - grosor2 / 2),
            size = Size(largo2, grosor2)
        )

        val heartCenterX = size.width * 0.80f
        val heartCenterY = size.height * 0.30f
        val heartSize = 55.dp.toPx()
        val heartPath = Path().apply {
            moveTo(heartCenterX, heartCenterY + heartSize * 0.55f)
            cubicTo(
                heartCenterX - heartSize * 0.9f, heartCenterY - heartSize * 0.1f,
                heartCenterX - heartSize * 0.45f, heartCenterY - heartSize * 0.75f,
                heartCenterX, heartCenterY - heartSize * 0.25f
            )
            cubicTo(
                heartCenterX + heartSize * 0.45f, heartCenterY - heartSize * 0.75f,
                heartCenterX + heartSize * 0.9f, heartCenterY - heartSize * 0.1f,
                heartCenterX, heartCenterY + heartSize * 0.55f
            )
        }
        drawPath(path = heartPath, color = colorMarcaAgua)

        val pulsoPath = Path().apply {
            moveTo(heartCenterX - heartSize * 0.8f, heartCenterY)
            lineTo(heartCenterX - heartSize * 0.25f, heartCenterY)
            lineTo(heartCenterX - heartSize * 0.1f, heartCenterY - heartSize * 0.35f)
            lineTo(heartCenterX + heartSize * 0.1f, heartCenterY + heartSize * 0.30f)
            lineTo(heartCenterX + heartSize * 0.25f, heartCenterY)
            lineTo(heartCenterX + heartSize * 0.8f, heartCenterY)
        }
        drawPath(
            path = pulsoPath,
            color = FondoMentaClaro,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val stethPath = Path().apply {
            moveTo(size.width * 0.62f, size.height * 0.10f)
            lineTo(size.width * 0.76f, size.height * 0.55f)
            quadraticBezierTo(
                size.width * 0.80f, size.height * 0.72f,
                size.width * 0.62f, size.height * 0.76f
            )
            quadraticBezierTo(
                size.width * 0.46f, size.height * 0.78f,
                size.width * 0.48f, size.height * 0.60f
            )
        }
        drawPath(
            path = stethPath,
            color = colorTrazo,
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
        )
        drawCircle(
            color = colorTrazo,
            radius = 22.dp.toPx(),
            center = Offset(size.width * 0.48f, size.height * 0.56f)
        )
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
        TextField(
            value = categoriaSeleccionada?.etiqueta ?: "Todas las categorías",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = "Categoría",
                    color = Color.White.copy(alpha = 0.85f)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
            },
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TarjetaGrisPizarra,
                unfocusedContainerColor = TarjetaGrisPizarra,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedTrailingIconColor = Color.White,
                unfocusedTrailingIconColor = Color.White
            ),
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val esBajoStock = producto.estado != EstadoProducto.DADO_DE_BAJA &&
            producto.cantidadDisponible < producto.nivelMinimo

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esBajoStock) TarjetaGrisAlerta else TarjetaGrisPizarra
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniatura visual del insumo médico por categoría
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AzulMarinoCappucare.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = producto.categoria.etiqueta,
                    tint = TurquesaStock,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (esBajoStock) {
                        Text(
                            text = "⚠️ Stock Bajo",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFB4AB),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Código: ${producto.codigo} | ${producto.categoria.etiqueta}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f)
                )
                Text(
                    text = "Ubicación: ${producto.ubicacion} | ${producto.estado}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Disponible: ${producto.cantidadDisponible} ${producto.unidadMedida} (Mín: ${producto.nivelMinimo})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (esBajoStock) Color(0xFFFFB4AB) else TurquesaStock
                )
            }
        }
    }
}

@Composable
fun TarjetaMovimiento(
    movimiento: Movimiento,
    nombreProducto: String,
    modifier: Modifier = Modifier
) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = TarjetaGrisPizarra)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = nombreProducto,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (esIngreso) "+${movimiento.cantidad} INGRESO" else "-${movimiento.cantidad} SALIDA",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (esIngreso) TurquesaStock else Color(0xFFFFB4AB)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fecha: ${movimiento.fecha} | Responsable: ${movimiento.responsable}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.88f)
            )
            movimiento.motivo?.let {
                Text(
                    text = "Motivo: ${it.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
            if (movimiento.observacion.isNotBlank()) {
                Text(
                    text = "Obs: ${movimiento.observacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
        }
    }
}