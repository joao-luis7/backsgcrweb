package br.com.ifba.prg03projetosgcr.pagamento.dto;

import br.com.ifba.prg03projetosgcr.pagamento.entity.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de entrada para registro e atualização de pagamentos.
 * Omite id e dataHora, que são controlados pelo servidor.
 *
 * @author João Luis
 */
public record PagamentoRequestDTO(
        @NotNull(message = "O pagamento deve estar vinculado a um cliente válido.")
        Long clienteId,

        @Positive(message = "O valor do pagamento deve ser maior que zero.")
        double valorTotal,

        @NotNull(message = "A forma de pagamento é obrigatória.")
        FormaPagamento formaPagamento
) {
}
