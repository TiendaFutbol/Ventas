package cl.tienda.ms.ventas.controller;

import cl.tienda.ms.ventas.dto.*;
import cl.tienda.ms.ventas.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import cl.tienda.ms.ventas.service.OrdenService;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@Tag(name = "Ordenes")
@SecurityRequirement(name = "bearerAuth")
public class OrdenController {
    private final OrdenService service;

    @GetMapping
    @Operation(summary = "Listar órdenes (admin: todas; cliente: propias)")
    public List<OrdenResponse> listar(@AuthenticationPrincipal Long usuarioId, Authentication auth) {
        return service.listar(usuarioId, esAdmin(auth));
    }

    @GetMapping("/mis-ordenes")
    public List<OrdenResponse> misOrdenes(@AuthenticationPrincipal Long usuarioId) {
        return service.listar(usuarioId, false);
    }

    @GetMapping("/{id}")
    public OrdenResponse buscar(@PathVariable("id") Long id, @AuthenticationPrincipal Long usuarioId, Authentication auth) {
        return service.detalle(id, usuarioId, esAdmin(auth));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear orden desde el carrito")
    public OrdenResponse crear(@AuthenticationPrincipal Long usuarioId, @RequestHeader("Authorization") String token,
            @Valid @RequestBody OrdenRequest request) {
        return service.crear(usuarioId, token, request);
    }

    @PostMapping("/{id}/pagar")
    @Operation(summary = "Simular pago y generar boleta de prueba")
    public OrdenResponse pagar(@PathVariable("id") Long id, @AuthenticationPrincipal Long usuarioId, Authentication auth) {
        return service.pagar(id, usuarioId, esAdmin(auth));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado (admin)")
    public OrdenResponse actualizar(@PathVariable("id") Long id, @Valid @RequestBody EstadoRequest request) {
        return service.actualizarEstado(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar orden pendiente o cancelada (admin)")
    public void eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
    }

    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
