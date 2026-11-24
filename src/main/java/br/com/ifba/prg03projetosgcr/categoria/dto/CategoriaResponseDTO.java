package br.com.ifba.prg03projetosgcr.categoria.dto;

import br.com.ifba.prg03projetosgcr.categoria.entity.Categoria;

/**
 * Dados de saída de uma categoria. Expõe o id para o front-end saber qual
 * registro foi criado/alterado e não inclui a coleção LAZY de produtos,
 * evitando LazyInitializationException na serialização JSON.
 *
 * @author João Luis
 */
public record CategoriaResponseDTO(
        Long id,
        String nome,
        String descricao,
        boolean ativo
) {

    public static CategoriaResponseDTO fromEntity(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isAtivo()
        );
    }
}
