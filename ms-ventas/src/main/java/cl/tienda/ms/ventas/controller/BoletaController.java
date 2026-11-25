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
import cl.tienda.ms.ventas.service.BoletaService;

@RestController
@RequestMapping("/api/boletas")
@RequiredArgsConstructor
@Tag(name = "Boletas")
@SecurityRequirement(name = "bearerAuth")
public class BoletaController {
    private final BoletaService service;

    @GetMapping
    @Operation(summary = "Listar boletas (admin: todas; cliente: propias)")
    public List<Boleta> listar(@AuthenticationPrincipal Long usuarioId, Authentication auth) {
        return service.listar(usuarioId, esAdmin(auth));
    }

    @GetMapping("/{id}")
    public Boleta buscar(@PathVariable("id") Long id, @AuthenticationPrincipal Long usuarioId, Authentication auth) {
        return service.buscar(id, usuarioId, esAdmin(auth));
    }

    @GetMapping("/orden/{ordenId}")
    public Boleta porOrden(@PathVariable("ordenId") Long ordenId, @AuthenticationPrincipal Long usuarioId, Authentication auth) {
        return service.porOrden(ordenId, usuarioId, esAdmin(auth));
    }

    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
