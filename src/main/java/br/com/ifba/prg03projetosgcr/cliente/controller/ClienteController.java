package br.com.ifba.prg03projetosgcr.cliente.controller;

import br.com.ifba.prg03projetosgcr.cliente.dto.ClienteRequestDTO;
import br.com.ifba.prg03projetosgcr.cliente.dto.ClienteResponseDTO;
import br.com.ifba.prg03projetosgcr.cliente.entity.Cliente;
import br.com.ifba.prg03projetosgcr.cliente.entity.Endereco;
import br.com.ifba.prg03projetosgcr.cliente.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST do módulo de clientes.
 * Recebe e devolve DTOs: a entidade Cliente nunca é exposta diretamente na fronteira HTTP
 * e seu saldoDevedor é protegido contra manipulação direta pelo payload.
 *
 * @author João Luis
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Cadastra um novo cliente com ou sem endereço. O cliente nasce ativo e com saldoDevedor zerado.
     * @param dto dados do cliente a ser cadastrado
     * @return 201 Created com os dados do cliente salvo, incluindo id e saldoDevedor inicial.
     */
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClienteResponseDTO> save(@Valid @RequestBody ClienteRequestDTO dto) {
        log.info("Requisição para salvar cliente recebida no controller: {}", dto.nome());

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setTelefone(dto.telefone());
        cliente.setAtivo(dto.ativo() != null ? dto.ativo() : true);
        cliente.setSaldoDevedor(0.0);

        if (dto.endereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setBairro(dto.endereco().bairro());
            endereco.setRua(dto.endereco().rua());
            endereco.setNumero(dto.endereco().numero());
            endereco.setPontoReferencia(dto.endereco().pontoReferencia());
            cliente.setEndereco(endereco);
        }

        Cliente salvo = clienteService.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponseDTO.fromEntity(salvo));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista todos os clientes ativos com seus respectivos endereços.
     * @return 200 OK com a lista de clientes ativos.
     */
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ClienteResponseDTO>> findAll() {
        log.debug("Requisição para listar todos os clientes recebida no controller");
        List<ClienteResponseDTO> response = clienteService.findAll().stream()
                .map(ClienteResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Atualiza os dados cadastrais e endereço de um cliente existente sem alterar seu saldoDevedor.
     * @param id identificador do cliente vindo da URL
     * @param dto novos dados do cliente
     * @return 200 OK com o cliente atualizado, ou 404 Not Found se o id não existir.
     */
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClienteResponseDTO> update(@PathVariable("id") Long id,
                                                     @Valid @RequestBody ClienteRequestDTO dto) {
        log.info("Requisição para atualizar cliente ID: {}", id);

        Cliente existente = clienteService.findById(id);

        existente.setNome(dto.nome());
        existente.setTelefone(dto.telefone());
        if (dto.ativo() != null) {
            existente.setAtivo(dto.ativo());
        }

        if (dto.endereco() != null) {
            Endereco endereco = existente.getEndereco();
            if (endereco == null) {
                endereco = new Endereco();
                existente.setEndereco(endereco);
            }
            endereco.setBairro(dto.endereco().bairro());
            endereco.setRua(dto.endereco().rua());
            endereco.setNumero(dto.endereco().numero());
            endereco.setPontoReferencia(dto.endereco().pontoReferencia());
        } else {
            existente.setEndereco(null);
        }

        Cliente atualizado = clienteService.update(existente);
        return ResponseEntity.status(HttpStatus.OK).body(ClienteResponseDTO.fromEntity(atualizado));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Inativa logicamente um cliente (ativo = false).
     * @param id identificador do cliente
     * @return 204 No Content, ou 404 Not Found se o id não existir.
     */
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("Requisição para inativar cliente ID: {} recebida no controller", id);
        clienteService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Busca clientes ativos por nome (parcial e ignorando maiúsculas/minúsculas).
     * @param nome termo de busca
     * @return 200 OK com a lista de clientes encontrados.
     */
    @GetMapping(path = "/findbynome", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ClienteResponseDTO>> findByNome(@RequestParam(name = "nome", required = false) String nome) {
        log.debug("Requisição para buscar clientes por nome: {} recebida no controller", nome);
        List<ClienteResponseDTO> response = clienteService.findByNome(nome).stream()
                .map(ClienteResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Busca um cliente pelo seu ID.
     * @param id identificador do cliente
     * @return 200 OK com os dados do cliente, ou 404 Not Found se não encontrado.
     */
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClienteResponseDTO> findById(@PathVariable("id") Long id) {
        log.debug("Requisição recebida para buscar cliente ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(ClienteResponseDTO.fromEntity(clienteService.findById(id)));
    }
}
