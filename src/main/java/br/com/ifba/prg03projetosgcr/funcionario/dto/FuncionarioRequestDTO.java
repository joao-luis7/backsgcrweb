package br.com.ifba.prg03projetosgcr.funcionario.dto;

import br.com.ifba.prg03projetosgcr.funcionario.entity.enums.PerfilAcesso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para cadastro de funcionários.
 * Não expõe o campo id, evitando manipulação direta via payload.
 *
 * @author João Luis
 */
public record FuncionarioRequestDTO(
        @NotBlank(message = "O nome do funcionário é obrigatório.")
        String nome,

        @NotBlank(message = "O username do funcionário é obrigatório.")
        String username,

        @NotBlank(message = "A senha do funcionário é obrigatória.")
        String senha,

        @NotNull(message = "O perfil de acesso do funcionário é obrigatório.")
        PerfilAcesso perfil,

        Boolean ativo
) {
}
