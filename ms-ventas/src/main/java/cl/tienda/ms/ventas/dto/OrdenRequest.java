package cl.tienda.ms.ventas.dto;

import jakarta.validation.constraints.*;

public record OrdenRequest(
    @NotBlank @Size(max = 100) String nombreEnvio,
    @NotBlank @Size(max = 100) String apellidoEnvio,
    @NotBlank @Email @Size(max = 150) String correoEnvio,
    @NotBlank @Size(max = 200) String direccion,
    @NotBlank @Size(max = 100) String region,
    @NotBlank @Size(max = 100) String comuna,
    @Size(max = 300) String indicaciones
) {}
