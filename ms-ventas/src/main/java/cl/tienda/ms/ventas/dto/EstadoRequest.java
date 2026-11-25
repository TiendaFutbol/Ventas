package cl.tienda.ms.ventas.dto;

import jakarta.validation.constraints.NotBlank;

public record EstadoRequest(@NotBlank String estado) {}
