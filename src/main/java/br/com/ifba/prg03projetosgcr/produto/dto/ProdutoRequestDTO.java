package br.com.ifba.prg03projetosgcr.produto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO de entrada para criação e atualização de produtos.
 * Não expõe o campo id, evitando manipulação direta via payload.
 *
 * @author João Luis
 */
public record ProdutoRequestDTO(
        @NotBlank(message = "O nome do produto é obrigatório.")
        String nome,

        String descricao,

        @Positive(message = "O preço de venda deve ser maior que zero.")
        double precoUnidade,

        @PositiveOrZero(message = "O preço de custo não pode ser negativo.")
        double precoCusto,

        @PositiveOrZero(message = "O estoque não pode ser negativo.")
        int quantidadeEstoque,

        Boolean ativo,

        Long categoriaId
) {
}
