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
@Table(name = "carrito")
@Getter
@Setter
@NoArgsConstructor
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "producto_id", nullable = false)
    private Long productoId;
    @Column(nullable = false)
    private Integer cantidad;
    @TimeZoneStorage(TimeZoneStorageType.NATIVE)
    @Column(name = "fecha_agregado")
    private OffsetDateTime fechaAgregado;
}
