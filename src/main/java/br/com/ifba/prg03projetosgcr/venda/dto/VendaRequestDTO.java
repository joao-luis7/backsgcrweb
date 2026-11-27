package br.com.ifba.prg03projetosgcr.venda.dto;

import br.com.ifba.prg03projetosgcr.pagamento.entity.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO de entrada para realização de venda.
 * Omite id, dataHora, valorTotal e statusVenda, que são controlados pelo servidor.
 *
 * @author João Luis
 */
public record VendaRequestDTO(
        Long clienteId,

        @NotNull(message = "A forma de pagamento é obrigatória.")
        FormaPagamento formaPagamento,

        @NotEmpty(message = "O carrinho está vazio, adicione produtos antes de finalizar.")
        @Valid
        List<ItemVendaRequestDTO> itens
) {
}
