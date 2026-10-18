package com.example.myapplicationcappucare.data

import androidx.room.TypeConverter
import com.example.myapplicationcappucare.model.CategoriaInsumo
import com.example.myapplicationcappucare.model.EstadoProducto

class Converters {

    @TypeConverter
    fun fromCategoria(categoria: CategoriaInsumo): String = categoria.name

    @TypeConverter
    fun toCategoria(value: String): CategoriaInsumo = try {
        CategoriaInsumo.valueOf(value)
    } catch (e: Exception) {
        CategoriaInsumo.values().first()
    }

    @TypeConverter
    fun fromEstado(estado: EstadoProducto): String = estado.name

    @TypeConverter
    fun toEstado(value: String): EstadoProducto = try {
        EstadoProducto.valueOf(value)
    } catch (e: Exception) {
        EstadoProducto.DISPONIBLE
    }
}