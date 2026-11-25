package cl.tienda.ms.ventas.client;

import cl.tienda.ms.ventas.dto.ProductoResponse;
import cl.tienda.ms.ventas.dto.OfertaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "catalogo", url = "${servicios.catalogo}")
public interface CatalogoClient {
    @GetMapping("/api/productos/{id}")
    ProductoResponse buscarProducto(@PathVariable("id") Long id);

    @GetMapping("/api/ofertas/vigentes")
    List<OfertaResponse> ofertasVigentes();
}
