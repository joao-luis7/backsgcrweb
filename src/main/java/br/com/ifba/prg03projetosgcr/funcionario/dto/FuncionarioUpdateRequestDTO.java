package br.com.ifba.prg03projetosgcr.funcionario.dto;

import br.com.ifba.prg03projetosgcr.funcionario.entity.enums.PerfilAcesso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para atualização cadastral de funcionários.
 * Não expõe o campo id (recebido pela URL) e torna senha/ativo opcionais:
 * quando omitidos ou em branco, os valores atuais da entidade são preservados.
 *
 * @author João Luis
 */
public record FuncionarioUpdateRequestDTO(
        @NotBlank(message = "O nome do funcionário é obrigatório.")
        String nome,

        @NotBlank(message = "O username do funcionário é obrigatório.")
        String username,

        String senha,

        @NotNull(message = "O perfil de acesso do funcionário é obrigatório.")
        PerfilAcesso perfil,

        Boolean ativo
) {
}
