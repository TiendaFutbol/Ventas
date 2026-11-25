package cl.tienda.ms.ventas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

@Entity
@Table(name = "ordenes")
@Getter
@Setter
@NoArgsConstructor
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @TimeZoneStorage(TimeZoneStorageType.NATIVE)
    @Column(nullable = false)
    private OffsetDateTime fecha;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    @Column(nullable = false, length = 20)
    private String estado;
    @Column(name = "nombre_envio", length = 100)
    private String nombreEnvio;
    @Column(name = "apellido_envio", length = 100)
    private String apellidoEnvio;
    @Column(name = "correo_envio", length = 150)
    private String correoEnvio;
    @Column(length = 200)
    private String direccion;
    @Column(length = 100)
    private String region;
    @Column(length = 100)
    private String comuna;
    @Column(length = 300)
    private String indicaciones;
}
