package br.com.ifba.prg03projetosgcr.produto.dto;

import br.com.ifba.prg03projetosgcr.produto.entity.Produto;

/**
 * DTO de saída para representação de produtos na API REST.
 * Inclui os dados resumidos da categoria associada para evitar LazyInitializationException.
 *
 * @author João Luis
 */
public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        double precoUnidade,
        double precoCusto,
        int quantidadeEstoque,
        boolean ativo,
        Long categoriaId,
        String categoriaNome
) {

    /**
     * Converte uma entidade Produto em ProdutoResponseDTO.
     *
     * @param produto entidade persistida
     * @return DTO preenchido com os dados do produto e da categoria (quando vinculada)
     */
    public static ProdutoResponseDTO fromEntity(Produto produto) {
        Long categoriaId = produto.getCategoria() != null ? produto.getCategoria().getId() : null;
        String categoriaNome = produto.getCategoria() != null ? produto.getCategoria().getNome() : null;

        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPrecoUnidade(),
                produto.getPrecoCusto(),
                produto.getQuantidadeEstoque(),
                produto.isAtivo(),
                categoriaId,
                categoriaNome
        );
    }
}
