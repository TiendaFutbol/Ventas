package cl.tienda.ms.ventas.model;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenItemId implements Serializable {
    private Long ordenId;
    private Long productoId;
}
