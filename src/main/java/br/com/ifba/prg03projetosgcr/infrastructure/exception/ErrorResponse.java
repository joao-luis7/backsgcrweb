package br.com.ifba.prg03projetosgcr.infrastructure.exception;

import java.time.LocalDateTime;

/**
 * Payload padronizado retornado pela API em respostas de erro HTTP.
 *
 * @author João Luis
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
