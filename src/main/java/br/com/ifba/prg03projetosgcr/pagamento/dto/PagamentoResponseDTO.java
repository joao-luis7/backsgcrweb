package br.com.ifba.prg03projetosgcr.pagamento.dto;

import br.com.ifba.prg03projetosgcr.pagamento.entity.FormaPagamento;
import br.com.ifba.prg03projetosgcr.pagamento.entity.Pagamento;
import java.time.LocalDateTime;

/**
 * DTO de saída para representação de recibos de pagamento na API REST.
 * Inclui dados do cliente e saldo devedor atualizado sem risco de LazyInitializationException.
 *
 * @author João Luis
 */
public record PagamentoResponseDTO(
        Long id,
        LocalDateTime dataHora,
        double valorTotal,
        FormaPagamento formaPagamento,
        Long clienteId,
        String clienteNome,
        double clienteSaldoDevedorAtual
) {

    /**
     * Converte uma entidade Pagamento em PagamentoResponseDTO.
     *
     * @param pagamento entidade persistida
     * @return DTO com os dados do recibo e do cliente associado
     */
    public static PagamentoResponseDTO fromEntity(Pagamento pagamento) {
        Long clienteId = pagamento.getCliente() != null ? pagamento.getCliente().getId() : null;
        String clienteNome = pagamento.getCliente() != null ? pagamento.getCliente().getNome() : null;
        double saldoAtual = pagamento.getCliente() != null ? pagamento.getCliente().getSaldoDevedor() : 0.0;

        return new PagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getDataHora(),
                pagamento.getValorTotal(),
                pagamento.getFormaPagamento(),
                clienteId,
                clienteNome,
                saldoAtual
        );
    }
}
