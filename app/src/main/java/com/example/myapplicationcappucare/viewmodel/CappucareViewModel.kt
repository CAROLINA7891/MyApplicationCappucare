package com.example.myapplicationcappucare.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationcappucare.data.AppDatabase
import com.example.myapplicationcappucare.data.MockData
import com.example.myapplicationcappucare.data.ProductoRepository
import com.example.myapplicationcappucare.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ErroresProducto(
    val codigo: String? = null,
    val nombre: String? = null,
    val categoria: String? = null,
    val unidadMedida: String? = null,
    val nivelMinimo: String? = null,
    val ubicacion: String? = null
) {
    val tieneErrores: Boolean
        get() = codigo != null || nombre != null || categoria != null ||
                unidadMedida != null || nivelMinimo != null || ubicacion != null
}

data class ErroresMovimiento(
    val producto: String? = null,
    val cantidad: String? = null,
    val motivo: String? = null
) {
    val tieneErrores: Boolean
        get() = producto != null || cantidad != null || motivo != null
}

class CappucareViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductoRepository

    // RF-05 / Room: Flow observado como StateFlow desde la base de datos
    val productos: StateFlow<List<Producto>>

    private val _movimientos = MutableStateFlow<List<Movimiento>>(MockData.movimientos)
    val movimientos: StateFlow<List<Movimiento>> = _movimientos.asStateFlow()

    private val _usuarioActivo = MutableStateFlow<Usuario?>(MockData.usuarios.firstOrNull())
    val usuarioActivo: StateFlow<Usuario?> = _usuarioActivo.asStateFlow()

    var errorLogin by mutableStateOf<String?>(null)
        private set

    init {
        val dao = AppDatabase.getDatabase(application).productoDao()
        repository = ProductoRepository(dao)

        productos = repository.todosLosProductos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Precarga de datos ficticios la primera vez si la base de datos está vacía
        viewModelScope.launch {
            repository.todosLosProductos.collect { lista ->
                if (lista.isEmpty()) {
                    repository.precargarDatosIniciales(MockData.productos)
                }
            }
        }
    }

    // --- RF-00: Autenticación ---
    fun login(usuarioIngresado: String, claveIngresada: String): Boolean {
        if (usuarioIngresado.isBlank()) {
            errorLogin = "Ingresa tu usuario"
            return false
        }
        if (claveIngresada.isBlank()) {
            errorLogin = "Ingresa tu clave"
            return false
        }

        val user = MockData.usuarios.find {
            it.usuario.trim().equals(usuarioIngresado.trim(), ignoreCase = true) && it.clave == claveIngresada
        }

        return if (user != null) {
            _usuarioActivo.value = user
            errorLogin = null
            true
        } else {
            errorLogin = "Usuario o clave incorrectos"
            false
        }
    }

    fun logout() {
        _usuarioActivo.value = null
    }

    // --- RF-01: Validar y Guardar Producto ---
    fun validarProducto(
        idActual: String?,
        codigo: String,
        nombre: String,
        categoria: CategoriaInsumo?,
        unidadMedida: String,
        nivelMinimoStr: String,
        ubicacion: String
    ): ErroresProducto {
        val min = nivelMinimoStr.toIntOrNull()
        val codigoExiste = productos.value.any {
            it.codigo.equals(codigo.trim(), ignoreCase = true) && it.id != idActual
        }

        return ErroresProducto(
            codigo = when {
                codigo.isBlank() -> "Campo obligatorio"
                codigoExiste -> "Ya existe un producto con ese código"
                else -> null
            },
            nombre = if (nombre.isBlank()) "Campo obligatorio" else null,
            categoria = if (categoria == null) "Elige una categoría" else null,
            unidadMedida = if (unidadMedida.isBlank()) "Campo obligatorio" else null,
            nivelMinimo = if (min == null || min < 0) "Ingresa un número válido" else null,
            ubicacion = if (ubicacion.isBlank()) "Campo obligatorio" else null
        )
    }

    fun guardarProducto(producto: Producto) {
        viewModelScope.launch {
            val listaActual = productos.value
            val index = listaActual.indexOfFirst { it.id == producto.id }

            if (index != -1) {
                // Edición: Preserva la cantidad disponible actual (RN-01)
                val productoExistente = listaActual[index]
                repository.guardarProducto(producto.copy(cantidadDisponible = productoExistente.cantidadDisponible))
            } else {
                // Producto Nuevo: Asigna ID correlativo y stock inicial 0
                val nuevoId = "P" + (listaActual.size + 1)
                repository.guardarProducto(
                    producto.copy(id = nuevoId, cantidadDisponible = 0, estado = EstadoProducto.DISPONIBLE)
                )
            }
        }
    }

    // --- RF-02 & RF-08: Búsqueda por Texto y Filtro por Categoría ---
    fun buscarProductos(texto: String, categoria: CategoriaInsumo?): List<Producto> {
        val query = texto.trim()
        return productos.value.filter { p ->
            val coincideTexto = query.isEmpty() ||
                    p.nombre.contains(query, ignoreCase = true) ||
                    p.codigo.contains(query, ignoreCase = true)
            val coincideCat = categoria == null || p.categoria == categoria
            coincideTexto && coincideCat
        }.sortedBy { it.nombre }
    }

    // --- RF-03: Actualizar Estado ---
    fun cambiarEstado(productoId: String, nuevoEstado: EstadoProducto) {
        val producto = productos.value.find { it.id == productoId } ?: return
        viewModelScope.launch {
            repository.actualizarStock(productoId, producto.cantidadDisponible, nuevoEstado)
        }
    }

    // --- RF-04 & RF-05: Validar y Registrar Movimiento ---
    fun validarMovimiento(
        productoId: String?,
        tipo: TipoMovimiento,
        cantidadStr: String,
        motivo: MotivoSalida?
    ): ErroresMovimiento {
        val producto = productos.value.find { it.id == productoId }
        val cantidad = cantidadStr.toIntOrNull()

        val errProd = if (producto == null) "Elige un producto" else null
        val errMotivo = if (tipo == TipoMovimiento.SALIDA && motivo == null) "Elige el motivo de la salida" else null

        val errCant = when {
            cantidad == null || cantidad <= 0 -> "Ingresa una cantidad mayor que 0"
            tipo == TipoMovimiento.SALIDA && producto != null && cantidad > producto.cantidadDisponible ->
                "Solo hay ${producto.cantidadDisponible} disponibles"
            else -> null
        }

        return ErroresMovimiento(producto = errProd, cantidad = errCant, motivo = errMotivo)
    }

    fun registrarMovimiento(
        productoId: String,
        tipo: TipoMovimiento,
        cantidad: Int,
        motivo: MotivoSalida?,
        observacion: String
    ) {
        val user = _usuarioActivo.value ?: return
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val nuevoMovimiento = Movimiento(
            id = "M" + (_movimientos.value.size + 1),
            productoId = productoId,
            fecha = fechaActual,
            tipo = tipo,
            cantidad = cantidad,
            motivo = if (tipo == TipoMovimiento.SALIDA) motivo else null,
            observacion = observacion,
            responsable = user.nombre
        )

        _movimientos.value = listOf(nuevoMovimiento) + _movimientos.value

        viewModelScope.launch {
            val producto = productos.value.find { it.id == productoId } ?: return@launch
            val nuevoStock = if (tipo == TipoMovimiento.INGRESO) {
                producto.cantidadDisponible + cantidad
            } else {
                producto.cantidadDisponible - cantidad
            }

            val nuevoEstado = if (nuevoStock == 0 && producto.estado == EstadoProducto.DISPONIBLE) {
                EstadoProducto.AGOTADO
            } else if (nuevoStock > 0 && producto.estado == EstadoProducto.AGOTADO) {
                EstadoProducto.DISPONIBLE
            } else {
                producto.estado
            }

            repository.actualizarStock(productoId, nuevoStock, nuevoEstado)
        }
    }

    // --- RF-09: Identificar Productos Bajo Mínimo ---
    fun productosBajoMinimo(): List<Producto> {
        return productos.value.filter {
            it.estado != EstadoProducto.DADO_DE_BAJA && it.cantidadDisponible < it.nivelMinimo
        }.sortedBy { it.cantidadDisponible }
    }

    // --- RF-10: Historial de Movimientos por Producto ---
    fun historialDe(productoId: String?): List<Movimiento> {
        val lista = if (productoId.isNullOrEmpty()) {
            _movimientos.value
        } else {
            _movimientos.value.filter { it.productoId == productoId }
        }
        return lista.sortedByDescending { it.fecha }
    }
}