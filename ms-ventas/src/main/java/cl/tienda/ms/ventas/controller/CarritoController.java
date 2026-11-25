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
import cl.tienda.ms.ventas.service.CarritoService;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito")
@SecurityRequirement(name = "bearerAuth")
public class CarritoController {
    private final CarritoService service;

    @GetMapping
    @Operation(summary = "Listar mi carrito")
    public List<Carrito> listar(@AuthenticationPrincipal Long usuarioId) {
        return service.listar(usuarioId);
    }

    @GetMapping("/{id}")
    public Carrito buscar(@PathVariable("id") Long id, @AuthenticationPrincipal Long usuarioId) {
        return service.buscar(id, usuarioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agregar producto al carrito")
    public Carrito agregar(@AuthenticationPrincipal Long usuarioId, @Valid @RequestBody CarritoRequest request) {
        return service.agregar(usuarioId, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cambiar cantidad")
    public Carrito actualizar(@PathVariable("id") Long id, @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody CantidadRequest request) {
        return service.actualizar(id, usuarioId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") Long id, @AuthenticationPrincipal Long usuarioId) {
        service.eliminar(id, usuarioId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Vaciar mi carrito")
    public void vaciar(@AuthenticationPrincipal Long usuarioId) {
        service.vaciar(usuarioId);
    }
}
