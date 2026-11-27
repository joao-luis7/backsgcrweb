package br.com.ifba.prg03projetosgcr.dashboard.dto;

import br.com.ifba.prg03projetosgcr.produto.dto.ProdutoResponseDTO;
import java.util.List;

/**
 * DTO de saída consolidado com os indicadores gerenciais do painel inicial.
 *
 * @author João Luis
 */
public record DashboardResumoResponseDTO(
        Double faturamentoDoDia,
        Long totalVendasDoDia,
        Long totalClientesAtivos,
        List<ProdutoResponseDTO> produtosComEstoqueBaixo
) {
}
