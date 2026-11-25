package cl.tienda.ms.ventas.exception;

import cl.tienda.ms.ventas.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> error(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse(e.getStatusCode().value(), e.getReason()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> conflicto() {
        return ResponseEntity.status(409).body(new ErrorResponse(409, "Registro duplicado o relacionado"));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> datosInvalidos() {
        return ResponseEntity.badRequest().body(new ErrorResponse(400, "Datos inválidos"));
    }
    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<ErrorResponse> errorExterno(feign.FeignException e) {
        int status = e.status();
        if (status == 404 || status == 401 || status == 403) {
            return ResponseEntity.status(status).body(new ErrorResponse(status, "El otro microservicio rechazó la consulta"));
        }
        return ResponseEntity.status(503).body(new ErrorResponse(503, "Identidad o catálogo no disponible"));
    }
}
