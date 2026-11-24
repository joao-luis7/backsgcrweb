package br.com.ifba.prg03projetosgcr.cliente.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada para cadastro e atualização de clientes.
 * Não expõe id nem saldoDevedor para impedir adulteração via payload HTTP.
 *
 * @author João Luis
 */
public record ClienteRequestDTO(
        @NotBlank(message = "O nome do cliente é obrigatório.")
        String nome,

        @NotBlank(message = "O telefone do cliente é obrigatório.")
        String telefone,

        Boolean ativo,

        @Valid
        EnderecoRequestDTO endereco
) {
}
