package org.idat.biblioteca.exception;

import org.idat.biblioteca.controller.PrestamoController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** Solo afecta al controlador de Préstamo; no interfiere con los módulos vecinos. */
@RestControllerAdvice(assignableTypes = PrestamoController.class)
public class PrestamoExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return respuesta(HttpStatus.BAD_REQUEST.value(), "Datos inválidos", errores);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> errorDeNegocio(ResponseStatusException ex) {
        return respuesta(ex.getStatusCode().value(), ex.getReason(), Map.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException ex) {
        return respuesta(HttpStatus.BAD_REQUEST.value(),
                "JSON inválido. Revisa los tipos de datos y las fechas (AAAA-MM-DD).", Map.of());
    }

    private ResponseEntity<Map<String, Object>> respuesta(int estado, String mensaje, Map<String, ?> detalle) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("fechaHora", LocalDateTime.now().toString());
        cuerpo.put("estado", estado);
        cuerpo.put("mensaje", mensaje);
        if (!detalle.isEmpty()) {
            cuerpo.put("errores", detalle);
        }
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
