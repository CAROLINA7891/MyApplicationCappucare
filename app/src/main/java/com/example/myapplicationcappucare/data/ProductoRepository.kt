package com.example.myapplicationcappucare.data

import com.example.myapplicationcappucare.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductoRepository(private val productoDao: ProductoDao) {

    val todosLosProductos: Flow<List<Producto>> = productoDao.obtenerTodos().map { lista ->
        lista.map { entity ->
            Producto(
                id = entity.id,
                codigo = entity.codigo,
                nombre = entity.nombre,
                categoria = entity.categoria,
                unidadMedida = entity.unidadMedida,
                nivelMinimo = entity.nivelMinimo,
                cantidadDisponible = entity.cantidadDisponible,
                ubicacion = entity.ubicacion,
                estado = entity.estado
            )
        }
    }

    suspend fun guardarProducto(producto: Producto) {
        val entity = ProductoEntity(
            id = producto.id,
            codigo = producto.codigo,
            nombre = producto.nombre,
            categoria = producto.categoria,
            unidadMedida = producto.unidadMedida,
            nivelMinimo = producto.nivelMinimo,
            cantidadDisponible = producto.cantidadDisponible,
            ubicacion = producto.ubicacion,
            estado = producto.estado
        )
        productoDao.insertarOActualizar(entity)
    }

    suspend fun actualizarStock(id: String, nuevoStock: Int, nuevoEstado: com.example.myapplicationcappucare.model.EstadoProducto) {
        productoDao.actualizarStockYEstado(id, nuevoStock, nuevoEstado)
    }

    suspend fun precargarDatosIniciales(productosIniciales: List<Producto>) {
        val entities = productosIniciales.map { producto ->
            ProductoEntity(
                id = producto.id,
                codigo = producto.codigo,
                nombre = producto.nombre,
                categoria = producto.categoria,
                unidadMedida = producto.unidadMedida,
                nivelMinimo = producto.nivelMinimo,
                cantidadDisponible = producto.cantidadDisponible,
                ubicacion = producto.ubicacion,
                estado = producto.estado
            )
        }
        productoDao.insertarTodos(entities)
    }
}