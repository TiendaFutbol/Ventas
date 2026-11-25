package cl.tienda.ms.ventas.dto;

import jakarta.validation.constraints.*;

public record CarritoRequest(@NotNull @Positive Long productoId, @NotNull @Min(1) @Max(999) Integer cantidad) {}
