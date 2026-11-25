package cl.tienda.ms.ventas.dto;

import java.math.BigDecimal;

public record ProductoResponse(Long id, String titulo, BigDecimal precio, Integer stock) {}
