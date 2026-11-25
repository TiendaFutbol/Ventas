package cl.tienda.ms.ventas.service;

import cl.tienda.ms.ventas.model.Boleta;
import cl.tienda.ms.ventas.repository.BoletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoletaService {
    private final BoletaRepository repository;
    private final OrdenService ordenes;

    public List<Boleta> listar(Long usuarioId, boolean admin) {
        if (admin) {
            return repository.findAll();
        }
        return ordenes.listar(usuarioId, false).stream()
                .map(o -> repository.findByOrdenId(o.getOrden().getId()))
                .flatMap(java.util.Optional::stream).toList();
    }

    public Boleta buscar(Long id, Long usuarioId, boolean admin) {
        Boleta boleta = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Boleta no encontrada"));
        ordenes.buscar(boleta.getOrdenId(), usuarioId, admin);
        return boleta;
    }

    public Boleta porOrden(Long ordenId, Long usuarioId, boolean admin) {
        ordenes.buscar(ordenId, usuarioId, admin);
        return repository.findByOrdenId(ordenId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Boleta no encontrada"));
    }
}
