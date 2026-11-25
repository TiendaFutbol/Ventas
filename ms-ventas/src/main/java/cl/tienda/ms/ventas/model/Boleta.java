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
@Table(name = "boletas")
@Getter
@Setter
@NoArgsConstructor
public class Boleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "orden_id", nullable = false, unique = true)
    private Long ordenId;
    @Column(nullable = false, unique = true, length = 50)
    private String folio;
    @TimeZoneStorage(TimeZoneStorageType.NATIVE)
    @Column(name = "fecha_emision", nullable = false)
    private OffsetDateTime fechaEmision;
}
