package com.example.myapplicationcappucare.model

enum class TipoMovimiento {
    INGRESO,
    SALIDA
}

enum class MotivoSalida {
    VENTA,
    VENCIMIENTO,
    MERMA,
    AJUSTE,
    OTRO
}

data class Movimiento(
    val id: String = "",
    val productoId: String,
    val fecha: String, // Formato "yyyy-MM-dd HH:mm"
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val motivo: MotivoSalida? = null,
    val observacion: String = "",
    val responsable: String
)