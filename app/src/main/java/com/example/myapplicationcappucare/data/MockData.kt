package com.example.myapplicationcappucare.data

import com.example.myapplicationcappucare.model.*

object MockData {

    val usuarios = listOf(
        Usuario(
            id = "U1",
            nombre = "Personal Encargado",
            usuario = "encargado@cappucare.cl",
            clave = "encargado123",
            perfil = Perfil.ENCARGADO
        ),
        Usuario(
            id = "U2",
            nombre = "Vendedor Autorizado",
            usuario = "ventas@cappucare.cl",
            clave = "ventas123",
            perfil = Perfil.USUARIO_AUTORIZADO
        )
    )

    val productos = mutableListOf(
        Producto("P1", "CUR-001", "Gasa estéril 10x10", "Gasa de algodón hidrófilo", CategoriaInsumo.CURACION, "cajas", 40, 15, "Estante A1", EstadoProducto.DISPONIBLE),
        Producto("P2", "CUR-002", "Venda elástica 10cm", "Venda de compresión media", CategoriaInsumo.CURACION, "unidades", 8, 15, "Estante A2", EstadoProducto.DISPONIBLE),
        Producto("P3", "PRO-001", "Guantes de nitrilo M", "Caja por 100 unidades", CategoriaInsumo.PROTECCION, "cajas", 50, 20, "Estante B1", EstadoProducto.DISPONIBLE),
        Producto("P4", "PRO-002", "Mascarilla KN95", "Empaque individual", CategoriaInsumo.PROTECCION, "unidades", 3, 10, "Estante B2", EstadoProducto.DISPONIBLE),
        Producto("P5", "MED-001", "Termómetro digital", "Medición infrarroja", CategoriaInsumo.MEDICION, "unidades", 12, 5, "Vitrina C1", EstadoProducto.DISPONIBLE),
        Producto("P6", "MED-002", "Oxímetro de pulso", "Sensor de dedo portátil", CategoriaInsumo.MEDICION, "unidades", 0, 3, "Vitrina C2", EstadoProducto.AGOTADO),
        Producto("P7", "HIG-001", "Alcohol gel 500ml", "Solución antiséptica 70%", CategoriaInsumo.HIGIENE, "frascos", 25, 10, "Estante D1", EstadoProducto.DISPONIBLE),
        Producto("P8", "ORT-001", "Cabestrillo adulto", "Soporte para inmovilización", CategoriaInsumo.ORTOPEDIA, "unidades", 4, 5, "Estante E1", EstadoProducto.DISPONIBLE)
    )

    val movimientos = mutableListOf(
        Movimiento("M1", "P1", "2026-10-01 09:30", TipoMovimiento.INGRESO, 50, null, "Recepcion inicial", "Personal Encargado"),
        Movimiento("M2", "P1", "2026-10-02 11:15", TipoMovimiento.SALIDA, 10, MotivoSalida.VENTA, "Venta Boleta #1024", "Vendedor Autorizado"),
        Movimiento("M3", "P3", "2026-10-03 14:20", TipoMovimiento.SALIDA, 5, MotivoSalida.VENTA, "Venta Factura #512", "Vendedor Autorizado"),
        Movimiento("M4", "P4", "2026-10-04 16:00", TipoMovimiento.SALIDA, 2, MotivoSalida.MERMA, "Empaque dañado", "Personal Encargado")
    )
}