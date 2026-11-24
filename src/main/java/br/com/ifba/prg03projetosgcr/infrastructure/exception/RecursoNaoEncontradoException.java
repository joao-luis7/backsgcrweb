package br.com.ifba.prg03projetosgcr.infrastructure.exception;

/**
 * Exceção lançada quando um recurso solicitado por identificador não é encontrado.
 *
 * @author João Luis
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
