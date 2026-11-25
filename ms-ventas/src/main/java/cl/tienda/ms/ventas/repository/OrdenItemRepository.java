package cl.tienda.ms.ventas.repository;

import cl.tienda.ms.ventas.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface OrdenItemRepository extends JpaRepository<OrdenItem, OrdenItemId> {
    List<OrdenItem> findByOrdenId(Long ordenId);
}
