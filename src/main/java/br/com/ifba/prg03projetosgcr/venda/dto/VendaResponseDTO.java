package br.com.ifba.prg03projetosgcr.venda.dto;

import br.com.ifba.prg03projetosgcr.pagamento.entity.FormaPagamento;
import br.com.ifba.prg03projetosgcr.venda.entity.Venda;
import br.com.ifba.prg03projetosgcr.venda.entity.enums.StatusVenda;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de saída para representação de vendas na API REST.
 *
 * @author João Luis
 */
public record VendaResponseDTO(
        Long id,
        LocalDateTime dataHora,
        double valorTotal,
        Long clienteId,
        String clienteNome,
        StatusVenda statusVenda,
        FormaPagamento formaPagamento,
        List<ItemVendaResponseDTO> itens
) {

    /**
     * Converte uma entidade Venda em VendaResponseDTO sem inicializar a coleção LAZY de itens.
     *
     * @param venda entidade persistida
     * @return DTO resumido da venda com lista de itens vazia
     */
    public static VendaResponseDTO fromEntity(Venda venda) {
        return buildDto(venda, List.of());
    }

    /**
     * Converte uma entidade Venda (com itens já carregados) em VendaResponseDTO completo.
     *
     * @param venda entidade persistida com itens carregados
     * @return DTO completo da venda incluindo seus itens
     */
    public static VendaResponseDTO fromEntityComItens(Venda venda) {
        List<ItemVendaResponseDTO> itensDto = venda.getItens() != null
                ? venda.getItens().stream().map(ItemVendaResponseDTO::fromEntity).toList()
                : List.of();
        return buildDto(venda, itensDto);
    }

    private static VendaResponseDTO buildDto(Venda venda, List<ItemVendaResponseDTO> itens) {
        Long clienteId = venda.getCliente() != null ? venda.getCliente().getId() : null;
        String clienteNome = venda.getCliente() != null ? venda.getCliente().getNome() : null;

        return new VendaResponseDTO(
                venda.getId(),
                venda.getDataHora(),
                venda.getValorTotal(),
                clienteId,
                clienteNome,
                venda.getStatusVenda(),
                venda.getFormaPagamento(),
                itens
        );
    }
}
