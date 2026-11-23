package com.example.myapplicationcappucare

import com.example.myapplicationcappucare.data.MockData
import com.example.myapplicationcappucare.data.MovimientoEntity
import com.example.myapplicationcappucare.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    // 1. Prueba de Autenticación (RF-00)
    @Test
    fun login_conCredencialesValidas_retornaUsuarioCorrecto() {
        val usuarioDemo = MockData.usuarios.first()
        val encontrado = MockData.usuarios.find {
            it.usuario.equals(usuarioDemo.usuario, ignoreCase = true) &&
                    it.clave == usuarioDemo.clave
        }

        assertNotNull("El usuario de prueba debe existir en MockData", encontrado)
        assertEquals(usuarioDemo.perfil, encontrado?.perfil)
    }

    @Test
    fun login_conClaveIncorrecta_retornaNull() {
        val usuarioDemo = MockData.usuarios.first()
        val encontrado = MockData.usuarios.find {
            it.usuario.equals(usuarioDemo.usuario, ignoreCase = true) &&
                    it.clave == "clave_erronea_123"
        }

        assertNull("No debe permitir acceso con clave incorrecta", encontrado)
    }

    // 2. Prueba de Validación de Producto y Códigos Duplicados (RF-01)
    @Test
    fun validarProducto_codigoDuplicado_detectaError() {
        val listaExistente = listOf(
            Producto(
                id = "P01",
                codigo = "PRO-001",
                nombre = "Guantes de nitrilo M",
                categoria = CategoriaInsumo.entries.first(),
                unidadMedida = "cajas",
                cantidadDisponible = 50,
                nivelMinimo = 20,
                ubicacion = "Estante B1",
                estado = EstadoProducto.DISPONIBLE
            )
        )

        val codigoNuevo = "pro-001"
        val existeDuplicado = listaExistente.any {
            it.codigo.equals(codigoNuevo.trim(), ignoreCase = true) && it.id != null
        }

        assertTrue("Debe detectar que el código PRO-001 ya está registrado", existeDuplicado)
    }

    // 3. Prueba de Cálculo de Stock y Validación de Salida (RF-04 & RF-05)
    @Test
    fun registrarSalida_cantidadMayorAlStock_noEsValida() {
        val stockDisponible = 10
        val cantidadSolicitadaSalida = 15

        val esSalidaValida = cantidadSolicitadaSalida in 1..stockDisponible

        assertFalse("No debe permitir retirar más unidades que el stock disponible", esSalidaValida)
    }

    @Test
    fun registrarMovimiento_ingresoYSalida_calculanStockCorrectamente() {
        var stock = 20
        val ingreso = 30
        val salida = 12

        stock += ingreso
        assertEquals(50, stock)

        stock -= salida
        assertEquals(38, stock)
    }

    // 4. Prueba de Detección de Productos Bajo Mínimo (RF-09)
    @Test
    fun productosBajoMinimo_filtraSoloActivosCriticos() {
        val categoriaPrueba = CategoriaInsumo.entries.first()
        val productos = listOf(
            Producto(
                id = "1",
                codigo = "INS-01",
                nombre = "Mascarilla KN95",
                categoria = categoriaPrueba,
                unidadMedida = "unidades",
                cantidadDisponible = 5,
                nivelMinimo = 10,
                ubicacion = "Bodega A",
                estado = EstadoProducto.DISPONIBLE
            ),
            Producto(
                id = "2",
                codigo = "INS-02",
                nombre = "Termómetro digital",
                categoria = categoriaPrueba,
                unidadMedida = "unidades",
                cantidadDisponible = 15,
                nivelMinimo = 5,
                ubicacion = "Vitrina C1",
                estado = EstadoProducto.DISPONIBLE
            ),
            Producto(
                id = "3",
                codigo = "INS-03",
                nombre = "Jeringa descontinuada",
                categoria = categoriaPrueba,
                unidadMedida = "cajas",
                cantidadDisponible = 0,
                nivelMinimo = 10,
                ubicacion = "Bodega Z",
                estado = EstadoProducto.DADO_DE_BAJA
            )
        )

        val criticos = productos.filter {
            it.estado != EstadoProducto.DADO_DE_BAJA && it.cantidadDisponible < it.nivelMinimo
        }

        assertEquals(1, criticos.size)
        assertEquals("Mascarilla KN95", criticos.first().nombre)
    }

    // 5. Prueba de Conversión Entidad Room <-> Modelo de Dominio (RF-10)
    @Test
    fun movimientoEntity_conversionRoomAModelo_conservaTodosLosDatos() {
        val movimientoOriginal = Movimiento(
            id = "M999",
            productoId = "P01",
            fecha = "2026-11-23 15:00",
            tipo = TipoMovimiento.SALIDA,
            cantidad = 4,
            motivo = MotivoSalida.VENTA,
            observacion = "Entrega sala urgencias",
            responsable = "Personal Encargado"
        )

        val entity = MovimientoEntity.fromModel(movimientoOriginal)
        val reconstruido = entity.toModel()

        assertEquals(movimientoOriginal.id, reconstruido.id)
        assertEquals(movimientoOriginal.productoId, reconstruido.productoId)
        assertEquals(movimientoOriginal.tipo, reconstruido.tipo)
        assertEquals(movimientoOriginal.cantidad, reconstruido.cantidad)
        assertEquals(movimientoOriginal.motivo, reconstruido.motivo)
        assertEquals(movimientoOriginal.responsable, reconstruido.responsable)
    }
}