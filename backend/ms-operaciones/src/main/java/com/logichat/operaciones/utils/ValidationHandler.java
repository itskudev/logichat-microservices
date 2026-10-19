package com.logichat.operaciones.utils;

import feign.FeignException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ValidationHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> errores = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        ApiResponse<Map<String, String>> response =
                new ApiResponse<>(false, "Errores de validación", errores);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ModeloNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> manejarModeloNotFoundException(ModeloNotFoundException ex, WebRequest request) {
        ApiResponse<String> response =
                new ApiResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> accesoDenegado(org.springframework.security.access.AccessDeniedException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, "No tiene permiso para esta operación", null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Feign sin fallback: 404 de personas -> 404 local, resto -> 502.
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiResponse<?>> feignNotFound(FeignException.NotFound ex) {
        ApiResponse<String> response =
                new ApiResponse<>(false, "Referencia en ms-personas no encontrada", null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<?>> feignError(FeignException ex) {
        ApiResponse<String> response =
                new ApiResponse<>(false, "ms-personas no disponible", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

    // ERROR 500 internal server error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        ApiResponse<String> response =
                new ApiResponse<>(false, "Error interno del servidor", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
