package cl.tienda.ms.ventas.client;

import cl.tienda.ms.ventas.dto.UsuarioResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identidad", url = "${servicios.identidad}")
public interface IdentidadClient {
    @GetMapping("/api/usuarios/me")
    UsuarioResponse miPerfil(@RequestHeader("Authorization") String token);
}
