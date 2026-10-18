package com.example.myapplicationcappucare.model

enum class CategoriaInsumo(val etiqueta: String) {
    CURACION("Curación"),
    PROTECCION("Protección personal"),
    INSTRUMENTAL("Instrumental"),
    MEDICION("Medición"),
    ORTOPEDIA("Ortopedia"),
    HIGIENE("Higiene"),
    OTRO("Otro")
}

enum class EstadoProducto {
    DISPONIBLE,
    AGOTADO,
    VENCIDO,
    DADO_DE_BAJA
}

data class Producto(
    val id: String = "",
    val codigo: String,
    val nombre: String,
    val descripcion: String = "",
    val categoria: CategoriaInsumo,
    val unidadMedida: String,
    val cantidadDisponible: Int = 0, // RN-01: Derivado automáticamente de los movimientos
    val nivelMinimo: Int,
    val ubicacion: String,
    val estado: EstadoProducto = EstadoProducto.DISPONIBLE
)