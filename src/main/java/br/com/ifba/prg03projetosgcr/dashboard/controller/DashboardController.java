package br.com.ifba.prg03projetosgcr.dashboard.controller;

import br.com.ifba.prg03projetosgcr.dashboard.dto.DashboardResumoResponseDTO;
import br.com.ifba.prg03projetosgcr.dashboard.service.DashboardService;
import br.com.ifba.prg03projetosgcr.produto.dto.ProdutoResponseDTO;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST do módulo de dashboard gerencial.
 * Expõe indicadores diários de faturamento, vendas, clientes ativos e alerta de estoque crítico.
 *
 * @author João Luis
 */
@RestController
@RequestMapping(path = "/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Obtém o faturamento total das vendas válidas realizadas no dia atual.
     * @return 200 OK com {"faturamentoDoDia": ...}.
     */
    @GetMapping(path = "/faturamento-dia", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Double>> obterFaturamentoDoDia() {
        log.debug("Requisição para obter faturamento do dia recebida no controller");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("faturamentoDoDia", dashboardService.obterFaturamentoDoDia()));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Obtém a quantidade total de vendas válidas realizadas no dia atual.
     * @return 200 OK com {"totalVendasDoDia": ...}.
     */
    @GetMapping(path = "/vendas-dia", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> obterTotalVendasDoDia() {
        log.debug("Requisição para obter total de vendas do dia recebida no controller");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("totalVendasDoDia", dashboardService.obterTotalVendasDoDia()));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista os produtos ativos com estoque crítico (menor ou igual a 5 unidades).
     * @return 200 OK com a lista de ProdutoResponseDTO.
     */
    @GetMapping(path = "/estoque-baixo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProdutoResponseDTO>> obterProdutosComEstoqueBaixo() {
        log.debug("Requisição para obter produtos com estoque baixo recebida no controller");
        List<ProdutoResponseDTO> produtos = dashboardService.obterProdutosComEstoqueBaixo().stream()
                .map(ProdutoResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(produtos);
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Obtém o total de clientes ativos identificados (excluindo "Não Identificado").
     * @return 200 OK com {"totalClientesAtivos": ...}.
     */
    @GetMapping(path = "/clientes-ativos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> obterTotalClientesAtivos() {
        log.debug("Requisição para obter total de clientes ativos recebida no controller");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("totalClientesAtivos", dashboardService.obterTotalClientesAtivos()));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Reúne todos os indicadores do dashboard em uma única chamada HTTP.
     * @return 200 OK com DashboardResumoResponseDTO consolidado.
     */
    @GetMapping(path = "/resumo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardResumoResponseDTO> obterResumo() {
        log.debug("Requisição para obter resumo consolidado do dashboard recebida no controller");
        List<ProdutoResponseDTO> estoqueBaixo = dashboardService.obterProdutosComEstoqueBaixo().stream()
                .map(ProdutoResponseDTO::fromEntity)
                .toList();

        DashboardResumoResponseDTO resumo = new DashboardResumoResponseDTO(
                dashboardService.obterFaturamentoDoDia(),
                dashboardService.obterTotalVendasDoDia(),
                dashboardService.obterTotalClientesAtivos(),
                estoqueBaixo
        );
        return ResponseEntity.status(HttpStatus.OK).body(resumo);
    }
}
