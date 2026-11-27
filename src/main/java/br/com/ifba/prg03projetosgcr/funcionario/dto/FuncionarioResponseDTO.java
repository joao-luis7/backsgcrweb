package br.com.ifba.prg03projetosgcr.funcionario.dto;

import br.com.ifba.prg03projetosgcr.funcionario.entity.Funcionario;
import br.com.ifba.prg03projetosgcr.funcionario.entity.enums.PerfilAcesso;

/**
 * DTO de saída para representação de funcionários na API REST.
 * Omite propositalmente o campo senha (invariante SEC-04).
 *
 * @author João Luis
 */
public record FuncionarioResponseDTO(
        Long id,
        String nome,
        String username,
        PerfilAcesso perfil,
        boolean ativo
) {

    /**
     * Converte uma entidade Funcionario em FuncionarioResponseDTO sem expor a senha.
     *
     * @param funcionario entidade persistida
     * @return DTO seguro com os dados públicos do funcionário
     */
    public static FuncionarioResponseDTO fromEntity(Funcionario funcionario) {
        return new FuncionarioResponseDTO(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getUsername(),
                funcionario.getPerfil(),
                funcionario.isAtivo()
        );
    }
}
