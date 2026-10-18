package com.example.myapplicationcappucare.data

import androidx.room.*
import com.example.myapplicationcappucare.model.EstadoProducto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: String): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOActualizar(producto: ProductoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<ProductoEntity>)

    @Query("UPDATE productos SET cantidadDisponible = :nuevoStock, estado = :nuevoEstado WHERE id = :id")
    suspend fun actualizarStockYEstado(id: String, nuevoStock: Int, nuevoEstado: EstadoProducto)

    @Delete
    suspend fun eliminar(producto: ProductoEntity)
}