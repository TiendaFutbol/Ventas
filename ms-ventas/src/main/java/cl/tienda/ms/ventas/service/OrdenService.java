package cl.tienda.ms.ventas.service;

import cl.tienda.ms.ventas.client.*;
import cl.tienda.ms.ventas.dto.*;
import cl.tienda.ms.ventas.model.*;
import cl.tienda.ms.ventas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class OrdenService {
    private final OrdenRepository ordenes;
    private final OrdenItemRepository items;
    private final CarritoRepository carrito;
    private final BoletaRepository boletas;
    private final CatalogoClient catalogo;
    private final IdentidadClient identidad;

    public List<OrdenResponse> listar(Long usuarioId, boolean admin) {
        List<Orden> lista = admin ? ordenes.findAll() : ordenes.findByUsuarioIdOrderByIdDesc(usuarioId);
        List<OrdenResponse> respuesta = new ArrayList<>();
        for (Orden orden : lista) {
            respuesta.add(respuesta(orden));
        }
        return respuesta;
    }

    public Orden buscar(Long id, Long usuarioId, boolean admin) {
        Orden orden = ordenes.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
        validarDueño(orden, usuarioId, admin);
        return orden;
    }

    public OrdenResponse detalle(Long id, Long usuarioId, boolean admin) {
        return respuesta(buscar(id, usuarioId, admin));
    }

    @Transactional
    public OrdenResponse crear(Long usuarioId, String token, OrdenRequest request) {
        UsuarioResponse usuario = identidad.miPerfil(token);
        if (!usuarioId.equals(usuario.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario inválido");
        }
        List<Carrito> productos = carrito.findByUsuarioIdOrderByIdAsc(usuarioId);
        if (productos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
        }
        List<OfertaResponse> ofertas = catalogo.ofertasVigentes();
        List<OrdenItem> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        HashSet<Long> vistos = new HashSet<>();
        for (Carrito item : productos) {
            if (!vistos.add(item.getProductoId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Hay productos duplicados en el carrito");
            }
            ProductoResponse producto = catalogo.buscarProducto(item.getProductoId());
            if (item.getCantidad() < 1 || item.getCantidad() > producto.stock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente: " + producto.titulo());
            }
            BigDecimal descuento = BigDecimal.ZERO;
            for (OfertaResponse oferta : ofertas) {
                if (oferta.productoId().equals(producto.id()) && oferta.descuentoPorcentaje().compareTo(descuento) > 0) {
                    descuento = oferta.descuentoPorcentaje();
                }
            }
            BigDecimal precio = producto.precio().multiply(BigDecimal.ONE.subtract(descuento.movePointLeft(2)))
                    .setScale(2, RoundingMode.HALF_UP);
            OrdenItem detalle = new OrdenItem();
            detalle.setProductoId(producto.id());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precio);
            detalles.add(detalle);
            total = total.add(precio.multiply(BigDecimal.valueOf(item.getCantidad())));
        }
        if (total.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El total supera el máximo permitido");
        }
        Orden orden = new Orden();
        orden.setUsuarioId(usuarioId);
        orden.setFecha(OffsetDateTime.now());
        orden.setTotal(total);
        orden.setEstado("pendiente");
        orden.setNombreEnvio(request.nombreEnvio());
        orden.setApellidoEnvio(request.apellidoEnvio());
        orden.setCorreoEnvio(request.correoEnvio());
        orden.setDireccion(request.direccion());
        orden.setRegion(request.region());
        orden.setComuna(request.comuna());
        orden.setIndicaciones(request.indicaciones());
        ordenes.saveAndFlush(orden);
        for (OrdenItem detalle : detalles) {
            detalle.setOrdenId(orden.getId());
            items.save(detalle);
        }
        carrito.deleteAll(productos);
        return new OrdenResponse(orden, detalles);
    }

    // Pago académico: no se conecta a un banco ni procesa dinero real.
    @Transactional
    public OrdenResponse pagar(Long id, Long usuarioId, boolean admin) {
        Orden orden = bloquear(id);
        validarDueño(orden, usuarioId, admin);
        if (!"pendiente".equals(orden.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden no está pendiente");
        }
        for (OrdenItem item : items.findByOrdenId(id)) {
            ProductoResponse producto = catalogo.buscarProducto(item.getProductoId());
            if (item.getCantidad() > producto.stock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente");
            }
        }
        orden.setEstado("pagada");
        ordenes.save(orden);
        Boleta boleta = new Boleta();
        boleta.setOrdenId(id);
        boleta.setFolio("BOLETA-" + id);
        boleta.setFechaEmision(OffsetDateTime.now());
        boletas.save(boleta);
        return respuesta(orden);
    }

    @Transactional
    public OrdenResponse actualizarEstado(Long id, EstadoRequest request) {
        Orden orden = bloquear(id);
        String actual = orden.getEstado();
        String nuevo = request.estado();
        if (!(actual.equals("pendiente") && nuevo.equals("cancelada"))
                && !(actual.equals("pagada") && nuevo.equals("enviada"))
                && !(actual.equals("enviada") && nuevo.equals("entregada"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cambio de estado no permitido; usa /pagar para pagar");
        }
        orden.setEstado(nuevo);
        return respuesta(ordenes.save(orden));
    }

    @Transactional
    public void eliminar(Long id) {
        Orden orden = bloquear(id);
        if (!List.of("pendiente", "cancelada").contains(orden.getEstado()) || boletas.findByOrdenId(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se elimina una orden con venta registrada");
        }
        items.deleteAll(items.findByOrdenId(id));
        items.flush();
        ordenes.delete(orden);
    }

    private Orden bloquear(Long id) {
        return ordenes.buscarParaActualizar(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
    }

    private void validarDueño(Orden orden, Long usuarioId, boolean admin) {
        if (!admin && !orden.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La orden pertenece a otro usuario");
        }
    }

    private OrdenResponse respuesta(Orden orden) {
        return new OrdenResponse(orden, items.findByOrdenId(orden.getId()));
    }
}
