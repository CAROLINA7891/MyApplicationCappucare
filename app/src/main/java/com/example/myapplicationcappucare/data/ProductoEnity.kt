package com.example.myapplicationcappucare.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplicationcappucare.model.CategoriaInsumo
import com.example.myapplicationcappucare.model.EstadoProducto

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey
    val id: String,
    val codigo: String,
    val nombre: String,
    val categoria: CategoriaInsumo,
    val unidadMedida: String,
    val nivelMinimo: Int,
    val cantidadDisponible: Int,
    val ubicacion: String,
    val estado: EstadoProducto
)