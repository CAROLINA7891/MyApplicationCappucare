package com.example.myapplicationcappucare.data

import androidx.room.TypeConverter
import com.example.myapplicationcappucare.model.CategoriaInsumo
import com.example.myapplicationcappucare.model.EstadoProducto
import com.example.myapplicationcappucare.model.MotivoSalida
import com.example.myapplicationcappucare.model.TipoMovimiento

class Converters {

    @TypeConverter
    fun fromCategoria(categoria: CategoriaInsumo): String = categoria.name

    @TypeConverter
    fun toCategoria(value: String): CategoriaInsumo = try {
        CategoriaInsumo.valueOf(value)
    } catch (e: Exception) {
        CategoriaInsumo.entries.first()
    }

    @TypeConverter
    fun fromEstado(estado: EstadoProducto): String = estado.name

    @TypeConverter
    fun toEstado(value: String): EstadoProducto = try {
        EstadoProducto.valueOf(value)
    } catch (e: Exception) {
        EstadoProducto.DISPONIBLE
    }

    @TypeConverter
    fun fromTipoMovimiento(tipo: TipoMovimiento): String = tipo.name

    @TypeConverter
    fun toTipoMovimiento(value: String): TipoMovimiento = try {
        TipoMovimiento.valueOf(value)
    } catch (e: Exception) {
        TipoMovimiento.INGRESO
    }

    @TypeConverter
    fun fromMotivoSalida(motivo: MotivoSalida?): String? = motivo?.name

    @TypeConverter
    fun toMotivoSalida(value: String?): MotivoSalida? = value?.let {
        try {
            MotivoSalida.valueOf(it)
        } catch (e: Exception) {
            null
        }
    }
}