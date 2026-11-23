package com.example.myapplicationcappucare.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplicationcappucare.model.MotivoSalida
import com.example.myapplicationcappucare.model.Movimiento
import com.example.myapplicationcappucare.model.TipoMovimiento

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey val id: String,
    val productoId: String,
    val fecha: String,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val motivo: MotivoSalida?,
    val observacion: String,
    val responsable: String
) {
    fun toModel(): Movimiento = Movimiento(
        id = id,
        productoId = productoId,
        fecha = fecha,
        tipo = tipo,
        cantidad = cantidad,
        motivo = motivo,
        observacion = observacion,
        responsable = responsable
    )

    companion object {
        fun fromModel(mov: Movimiento): MovimientoEntity = MovimientoEntity(
            id = mov.id,
            productoId = mov.productoId,
            fecha = mov.fecha,
            tipo = mov.tipo,
            cantidad = mov.cantidad,
            motivo = mov.motivo,
            observacion = mov.observacion,
            responsable = mov.responsable
        )
    }
}