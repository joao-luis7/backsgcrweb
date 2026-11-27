package br.com.ifba.prg03projetosgcr.pagamento.controller;

import br.com.ifba.prg03projetosgcr.cliente.entity.Cliente;
import br.com.ifba.prg03projetosgcr.cliente.service.ClienteService;
import br.com.ifba.prg03projetosgcr.pagamento.dto.PagamentoRequestDTO;
import br.com.ifba.prg03projetosgcr.pagamento.dto.PagamentoResponseDTO;
import br.com.ifba.prg03projetosgcr.pagamento.entity.Pagamento;
import br.com.ifba.prg03projetosgcr.pagamento.service.PagamentoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST do módulo de pagamentos.
 * Recebe e devolve DTOs para proteger o saldo devedor do cliente e evitar erros de carregamento LAZY.
 *
 * @author João Luis
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final ClienteService clienteService;

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Registra um novo pagamento e abate o valor do saldo devedor do cliente (quando não FIADO).
     * @param dto dados do pagamento (clienteId, valorTotal e formaPagamento)
     * @return 201 Created com o recibo gerado e o saldo atualizado do cliente.
     */
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagamentoResponseDTO> registrarPagamento(@Valid @RequestBody PagamentoRequestDTO dto) {
        log.info("Requisição para registrar pagamento recebida no controller. Valor: R$ {}", dto.valorTotal());

        Cliente cliente = clienteService.findById(dto.clienteId());

        Pagamento pagamento = new Pagamento();
        pagamento.setCliente(cliente);
        pagamento.setValorTotal(dto.valorTotal());
        pagamento.setFormaPagamento(dto.formaPagamento());

        Pagamento salvo = pagamentoService.registrarPagamento(pagamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(PagamentoResponseDTO.fromEntity(salvo));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista todos os pagamentos registrados com os dados do cliente.
     * @return 200 OK com a lista de pagamentos.
     */
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PagamentoResponseDTO>> findAll() {
        log.debug("Requisição para listar todos os pagamentos recebida no controller");
        return ResponseEntity.status(HttpStatus.OK).body(toResponseList(pagamentoService.findAll()));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Busca um recibo de pagamento pelo seu ID.
     * @param id identificador do pagamento
     * @return 200 OK com os dados do pagamento, ou 404 Not Found se o id não existir.
     */
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagamentoResponseDTO> findById(@PathVariable("id") Long id) {
        log.debug("Requisição para buscar pagamento ID: {} recebida no controller", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(PagamentoResponseDTO.fromEntity(pagamentoService.findById(id)));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista o histórico de pagamentos de um cliente específico.
     * @param idCliente identificador do cliente
     * @return 200 OK com os pagamentos vinculados ao cliente.
     */
    @GetMapping(path = "/cliente/{idCliente}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PagamentoResponseDTO>> findByClienteId(@PathVariable("idCliente") Long idCliente) {
        log.debug("Requisição para buscar pagamentos do cliente ID: {} recebida no controller", idCliente);
        return ResponseEntity.status(HttpStatus.OK)
                .body(toResponseList(pagamentoService.findByClienteId(idCliente)));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Atualiza valor e forma de pagamento de um recibo existente, preservando dataHora e cliente originais
     * e recalculando o saldo devedor do cliente na mesma transação.
     * @param id identificador do pagamento vindo da URL
     * @param dto novos dados do pagamento
     * @return 200 OK com o recibo atualizado, ou 404 Not Found se o id não existir.
     */
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagamentoResponseDTO> update(@PathVariable("id") Long id,
                                                       @Valid @RequestBody PagamentoRequestDTO dto) {
        log.info("Requisição para atualizar pagamento ID: {} recebida no controller", id);

        Pagamento pagamento = pagamentoService.findById(id);
        pagamento.setValorTotal(dto.valorTotal());
        pagamento.setFormaPagamento(dto.formaPagamento());

        Pagamento atualizado = pagamentoService.update(pagamento);
        return ResponseEntity.status(HttpStatus.OK).body(PagamentoResponseDTO.fromEntity(atualizado));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Exclui um recibo de pagamento e restaura o valor no saldo devedor do cliente (quando não FIADO).
     * @param id identificador do pagamento
     * @return 204 No Content, ou 404 Not Found se o id não existir.
     */
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("Requisição para deletar pagamento ID: {} recebida no controller", id);
        pagamentoService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Calcula o faturamento total de pagamentos registrados nos últimos 30 dias.
     * @return 200 OK com o valor consolidado em {"faturamentoUltimos30Dias": ...}.
     */
    @GetMapping(path = "/faturamento-30-dias", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Double>> getFaturamentoUltimos30Dias() {
        log.debug("Requisição recebida para calcular o faturamento dos últimos 30 dias");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("faturamentoUltimos30Dias", pagamentoService.calcularFaturamentoUltimos30Dias()));
    }

    private List<PagamentoResponseDTO> toResponseList(List<Pagamento> pagamentos) {
        return pagamentos.stream().map(PagamentoResponseDTO::fromEntity).toList();
    }
}
