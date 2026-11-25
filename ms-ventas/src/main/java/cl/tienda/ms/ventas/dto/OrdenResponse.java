package cl.tienda.ms.ventas.dto;

import cl.tienda.ms.ventas.model.Orden;
import cl.tienda.ms.ventas.model.OrdenItem;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class OrdenResponse {
    @JsonUnwrapped
    private Orden orden;
    private List<OrdenItem> items;
}
