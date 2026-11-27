package br.com.ifba.prg03projetosgcr.venda.dto;

import br.com.ifba.prg03projetosgcr.venda.entity.ItemVenda;

/**
 * DTO de saída para representação de um item de venda.
 *
 * @author João Luis
 */
public record ItemVendaResponseDTO(
        Long id,
        Long produtoId,
        String produtoNome,
        int quantidade,
        double precoUnitario,
        double subtotal
) {

    /**
     * Converte uma entidade ItemVenda em ItemVendaResponseDTO.
     *
     * @param item entidade persistida
     * @return DTO com os dados do item e do produto associado
     */
    public static ItemVendaResponseDTO fromEntity(ItemVenda item) {
        Long produtoId = item.getProduto() != null ? item.getProduto().getId() : null;
        String produtoNome = item.getProduto() != null ? item.getProduto().getNome() : null;

        return new ItemVendaResponseDTO(
                item.getId(),
                produtoId,
                produtoNome,
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal()
        );
    }
}
