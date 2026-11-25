# ms-ventas

Java 17, Spring Boot 3.5.8, Spring Cloud 2025.0.1, Oracle, JWT, Swagger. Puerto 8083.
Ejecutar el SQL de ventas del informe y levantar identidad (8081), catálogo (8082) y ventas (8083). Abrir la carpeta que contiene pom.xml. Ejecutar MsVentasApplication.java o `mvn spring-boot:run`.
Swagger: http://localhost:8083/swagger-ui.html

Esquema C##ms_ventas. application-local.yml es local y está ignorado por Git. No publicar secretos.

## Pruebas manuales Postman
Todas las rutas requieren access token de identidad. Usar un cliente de prueba.
1. POST /api/carrito {"productoId":1,"cantidad":2} -> 201. Guardar id real del item.
2. GET /api/carrito -> 200. PUT /api/carrito/{id} {"cantidad":1} -> 200. DELETE /api/carrito/{id} -> 204. Volver a agregar un producto.
3. POST /api/ordenes con body:
   {"nombreEnvio":"Ana","apellidoEnvio":"Perez","correoEnvio":"ana@example.com","direccion":"Calle 123","region":"Región Metropolitana de Santiago","comuna":"Santiago","indicaciones":"Entregar en la tarde"}
   -> 201, estado pendiente. Guardar id de orden. El carrito se vacía y el precio queda registrado desde catálogo, no desde Postman.
4. GET /api/ordenes/{id} -> 200. POST /api/ordenes/{id}/pagar (sin body) -> 200, estado pagada, pago simulado.
5. GET /api/boletas/orden/{id} -> 200, boleta de prueba.
6. Con token admin: PUT /api/ordenes/{id}/estado {"estado":"enviada"} -> 200, después "entregada" -> 200.
7. Para probar DELETE /api/ordenes/{id}, crear otra orden pendiente y eliminarla como admin -> 204. No se pueden eliminar órdenes pagadas ni boletas.
8. GET /api/ordenes sin token -> 401; PUT estado con token cliente -> 403; otro cliente no puede ver órdenes ajenas.

## Alcance académico
El pago y la boleta son simulados (no pago real ni documento tributario). Se verifica stock al agregar, crear orden y pagar; no se reserva ni descuenta stock automáticamente. La coordinación distribuida de inventario queda pendiente y no debe usarse en producción. Se aplica la mayor oferta vigente del producto y se congela el precio al crear la orden. No hay integración con notificaciones todavía. Las órdenes pagadas no se pueden borrar. Se incluye el estado entregada porque aparece en el SQL.
