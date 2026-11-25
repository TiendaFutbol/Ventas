package cl.tienda.ms.ventas.dto;

import java.math.BigDecimal;

public record OfertaResponse(Long productoId, BigDecimal descuentoPorcentaje) {}
