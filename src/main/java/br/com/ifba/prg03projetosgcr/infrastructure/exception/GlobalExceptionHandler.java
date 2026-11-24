package br.com.ifba.prg03projetosgcr.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tratador global de exceções da API REST, convertendo falhas de negócio,
 * validação e erros internos em respostas JSON padronizadas sem expor stack traces.
 *
 * @author João Luis
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNaoEncontradoException(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request) {
        log.warn("Recurso não encontrado em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponse> handleRegraNegocioException(
            RegraNegocioException ex,
            HttpServletRequest request) {
        log.warn("Violação de regra de negócio em {}: {}", request.getRequestURI(), ex.getMessage());
        String mensagem = ex.getMessage() != null ? ex.getMessage() : "Violação de regra de negócio.";
        HttpStatus status = (mensagem.contains("Credenciais inválidas") || mensagem.contains("Usuário inativo"))
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.BAD_REQUEST;
        return buildResponse(status, mensagem, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        log.warn("Argumento inválido ou recurso não encontrado em {}: {}", request.getRequestURI(), ex.getMessage());
        String mensagem = ex.getMessage() != null ? ex.getMessage() : "Argumento inválido.";
        String lower = mensagem.toLowerCase();
        HttpStatus status = (lower.contains("não encontrad") || lower.contains("inexistente"))
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return buildResponse(status, mensagem, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Erro de validação do DTO em {}: {}", request.getRequestURI(), mensagem);
        return buildResponse(HttpStatus.BAD_REQUEST, mensagem, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        log.warn("Payload JSON malformado ou enum inválido em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição JSON inválido ou com formato incorreto.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Erro interno inesperado em {}", request.getRequestURI(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor.",
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
