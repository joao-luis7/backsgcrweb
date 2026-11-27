package br.com.ifba.prg03projetosgcr.venda.controller;

import br.com.ifba.prg03projetosgcr.cliente.entity.Cliente;
import br.com.ifba.prg03projetosgcr.cliente.service.ClienteService;
import br.com.ifba.prg03projetosgcr.produto.entity.Produto;
import br.com.ifba.prg03projetosgcr.produto.service.ProdutoService;
import br.com.ifba.prg03projetosgcr.venda.dto.ItemVendaRequestDTO;
import br.com.ifba.prg03projetosgcr.venda.dto.VendaRequestDTO;
import br.com.ifba.prg03projetosgcr.venda.dto.VendaResponseDTO;
import br.com.ifba.prg03projetosgcr.venda.entity.ItemVenda;
import br.com.ifba.prg03projetosgcr.venda.entity.Venda;
import br.com.ifba.prg03projetosgcr.venda.service.VendaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST do módulo de vendas.
 * Recebe e devolve DTOs, delegando o cálculo financeiro e validação de regras ao servidor.
 *
 * @author João Luis
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/vendas")
public class VendaController {

    private final VendaService vendaService;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Realiza uma nova venda recalculando preços unitários, subtotais e valor total no servidor.
     * Vendas à vista sem cliente são vinculadas automaticamente ao cliente "Não Identificado".
     * @param dto dados da venda e itens do carrinho
     * @return 201 Created com a venda registrada e seus itens.
     */
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VendaResponseDTO> realizarVenda(@Valid @RequestBody VendaRequestDTO dto) {
        log.info("Requisição recebida para realizar nova venda");

        Venda venda = new Venda();
        venda.setFormaPagamento(dto.formaPagamento());

        if (dto.clienteId() != null) {
            Cliente cliente = clienteService.findById(dto.clienteId());
            venda.setCliente(cliente);
        }

        for (ItemVendaRequestDTO itemDto : dto.itens()) {
            Produto produto = produtoService.findById(itemDto.produtoId());
            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemDto.quantidade());
            item.setVenda(venda);
            venda.getItens().add(item);
        }

        Venda salva = vendaService.realizarVenda(venda);
        return ResponseEntity.status(HttpStatus.CREATED).body(VendaResponseDTO.fromEntityComItens(salva));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista todas as vendas cadastradas com os dados resumidos do cliente.
     * @return 200 OK com a lista de vendas.
     */
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VendaResponseDTO>> findAll() {
        log.debug("Requisição recebida para buscar todas as vendas");
        List<VendaResponseDTO> response = vendaService.findAll().stream()
                .map(VendaResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Busca uma venda detalhada pelo ID, incluindo todos os itens e produtos.
     * @param id identificador da venda
     * @return 200 OK com a venda e seus itens, ou 404 Not Found se o id não existir.
     */
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VendaResponseDTO> findByIdComItens(@PathVariable("id") Long id) {
        log.debug("Requisição recebida para buscar venda detalhada (com itens) ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(VendaResponseDTO.fromEntityComItens(vendaService.findByIdComItens(id)));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Cancela uma venda existente, devolvendo o estoque dos produtos e estornando o saldo devedor se FIADO.
     * @param id identificador da venda
     * @return 204 No Content, 400 Bad Request se já cancelada, ou 404 Not Found se o id não existir.
     */
    @PatchMapping(path = "/cancelar/{id}")
    public ResponseEntity<Void> cancelarVenda(@PathVariable("id") Long id) {
        log.info("Requisição recebida para cancelar a venda ID: {}", id);
        vendaService.cancelarVenda(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
