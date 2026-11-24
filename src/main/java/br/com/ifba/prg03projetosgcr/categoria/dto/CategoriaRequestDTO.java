package br.com.ifba.prg03projetosgcr.categoria.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada para cadastro e atualização de categorias.
 * Não possui o campo id: na atualização o identificador vem apenas da URL,
 * impedindo que o cliente HTTP escolha qual registro será sobrescrito.
 *
 * @author João Luis
 */
public record CategoriaRequestDTO(

        @NotBlank(message = "O nome da categoria é obrigatório.")
        String nome,

        String descricao,

        // Opcional: no cadastro assume true; na atualização mantém o valor atual se vier nulo
        Boolean ativo
) {
}
