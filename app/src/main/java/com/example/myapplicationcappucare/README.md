package com.example.myapplicationcappucare

# EP2 - Inventario Médico Cappucare

## Integrantes
- [Nombre Integrante 1]
- [Nombre Integrante 2]

## Repositorio GitHub
[Pega aquí el enlace a tu repositorio de GitHub]

## Instrucciones de Ejecución
1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle (Sync Project with Gradle Files).
3. Ejecutar en emulador o dispositivo físico con Android 8.0 (API 26) o superior.

## Usuarios de Prueba
- **Personal Encargado:** `encargado` / `1234`
- **Personal de Salud:** `personal` / `1234`

## Requerimientos Funcionales Implementados
- **RF-00:** Autenticación de Usuarios.
- **RF-01:** Registro y Edición de Productos con validaciones.
- **RF-02:** Filtrado por Categorías.
- **RF-04/05:** Movimientos de Stock (Ingresos y Salidas).
- **RF-08:** Búsqueda por Nombre y Código.
- **RF-09:** Alerta y resaltado visual de Productos en Stock Bajo.
- **Persistencia Local:** Room Database (SQLite).

## Recurso Nativo Utilizado
- **Recurso:** Notificaciones Locales de Stock Bajo
- **Justificación:** Alerta al personal médico sobre 
- la escasez crítica de insumos médicos en tiempo real en el dispositivo 
- sin depender de servicios externos.