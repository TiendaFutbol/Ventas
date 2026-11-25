package cl.tienda.ms.ventas.service;

import cl.tienda.ms.ventas.client.CatalogoClient;
import cl.tienda.ms.ventas.dto.*;
import cl.tienda.ms.ventas.model.Carrito;
import cl.tienda.ms.ventas.repository.CarritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {
    private final CarritoRepository repository;
    private final CatalogoClient catalogo;

    public List<Carrito> listar(Long usuarioId) {
        return repository.findByUsuarioIdOrderByIdAsc(usuarioId);
    }

    public Carrito buscar(Long id, Long usuarioId) {
        Carrito item = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado"));
        if (!item.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El item pertenece a otro usuario");
        }
        return item;
    }

    @Transactional
    public Carrito agregar(Long usuarioId, CarritoRequest request) {
        Carrito item = repository.findByUsuarioIdAndProductoId(usuarioId, request.productoId())
                .orElse(new Carrito());
        int cantidad = request.cantidad();
        if (item.getId() != null) {
            cantidad += item.getCantidad();
        }
        validarStock(request.productoId(), cantidad);
        item.setUsuarioId(usuarioId);
        item.setProductoId(request.productoId());
        item.setCantidad(cantidad);
        item.setFechaAgregado(OffsetDateTime.now());
        return repository.save(item);
    }

    @Transactional
    public Carrito actualizar(Long id, Long usuarioId, CantidadRequest request) {
        Carrito item = buscar(id, usuarioId);
        validarStock(item.getProductoId(), request.cantidad());
        item.setCantidad(request.cantidad());
        return repository.save(item);
    }

    @Transactional
    public void eliminar(Long id, Long usuarioId) {
        repository.delete(buscar(id, usuarioId));
    }

    @Transactional
    public void vaciar(Long usuarioId) {
        repository.deleteAll(listar(usuarioId));
    }

    private void validarStock(Long productoId, int cantidad) {
        ProductoResponse producto = catalogo.buscarProducto(productoId);
        if (cantidad > 999 || cantidad > producto.stock()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente o cantidad inválida");
        }
    }
}
