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
@Table(name = "orden_items")
@IdClass(OrdenItemId.class)
@Getter
@Setter
@NoArgsConstructor
public class OrdenItem {
    @Id
    @Column(name = "orden_id")
    private Long ordenId;
    @Id
    @Column(name = "producto_id")
    private Long productoId;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
}
