package br.com.ifba.prg03projetosgcr.funcionario.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para autenticação de funcionários via corpo JSON (POST),
 * evitando exposição de credenciais em URLs ou logs de acesso.
 *
 * @author João Luis
 */
public record LoginRequestDTO(
        @NotBlank(message = "O username do funcionário é obrigatório.")
        String username,

        @NotBlank(message = "A senha do funcionário é obrigatória.")
        String senha
) {
}
