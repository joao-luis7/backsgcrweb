package br.com.ifba.prg03projetosgcr.funcionario.controller;

import br.com.ifba.prg03projetosgcr.funcionario.dto.FuncionarioRequestDTO;
import br.com.ifba.prg03projetosgcr.funcionario.dto.FuncionarioResponseDTO;
import br.com.ifba.prg03projetosgcr.funcionario.dto.FuncionarioUpdateRequestDTO;
import br.com.ifba.prg03projetosgcr.funcionario.dto.LoginRequestDTO;
import br.com.ifba.prg03projetosgcr.funcionario.entity.Funcionario;
import br.com.ifba.prg03projetosgcr.funcionario.service.FuncionarioService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST do módulo de funcionários.
 * Recebe e devolve DTOs para garantir que credenciais não trafeguem em URLs
 * e que o campo senha jamais seja serializado nas respostas JSON (SEC-04).
 *
 * @author João Luis
 */
@RestController
@RequestMapping(path = "/funcionarios")
@RequiredArgsConstructor
@Slf4j
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Autentica um funcionário com username e senha enviados no corpo da requisição.
     * @param dto credenciais de acesso do funcionário
     * @return 200 OK com os dados públicos do funcionário (sem senha), ou 401 Unauthorized se inválido.
     */
    @PostMapping(path = "/autenticar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuncionarioResponseDTO> autenticar(@Valid @RequestBody LoginRequestDTO dto) {
        log.debug("Requisição de autenticação recebida no controller para o username: {}", dto.username());
        Funcionario autenticado = funcionarioService.autenticar(dto.username(), dto.senha());
        return ResponseEntity.status(HttpStatus.OK).body(FuncionarioResponseDTO.fromEntity(autenticado));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Cadastra um novo funcionário. Se "ativo" não for informado, o funcionário nasce ativo.
     * @param dto dados do funcionário a ser cadastrado (sem id)
     * @return 201 Created com os dados públicos do funcionário criado (sem senha).
     */
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuncionarioResponseDTO> save(@Valid @RequestBody FuncionarioRequestDTO dto) {
        log.info("Requisição para salvar funcionário recebida no controller: {}", dto.username());

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.nome());
        funcionario.setUsername(dto.username());
        funcionario.setSenha(dto.senha());
        funcionario.setPerfil(dto.perfil());
        funcionario.setAtivo(dto.ativo() != null ? dto.ativo() : true);

        funcionarioService.save(funcionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(FuncionarioResponseDTO.fromEntity(funcionario));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista todos os funcionários cadastrados (ativos e inativos).
     * @return 200 OK com a lista de dados públicos dos funcionários (sem senha).
     */
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FuncionarioResponseDTO>> findAll() {
        log.debug("Requisição para listar todos os funcionários recebida no controller.");
        return ResponseEntity.ok(toResponseList(funcionarioService.findAll()));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista apenas os funcionários ativos no sistema.
     * @return 200 OK com a lista de dados públicos dos funcionários ativos (sem senha).
     */
    @GetMapping(path = "/ativos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FuncionarioResponseDTO>> findAllAtivos() {
        log.debug("Requisição para listar funcionários ativos recebida no controller.");
        return ResponseEntity.ok(toResponseList(funcionarioService.findAllAtivos()));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Busca um funcionário pelo seu identificador único.
     * @param id identificador do funcionário
     * @return 200 OK com os dados públicos do funcionário (sem senha), ou 404 Not Found se não existir.
     */
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuncionarioResponseDTO> findById(@PathVariable("id") Long id) {
        log.debug("Requisição para buscar funcionário pelo ID {} recebida no controller.", id);
        Funcionario funcionario = funcionarioService.findById(id);
        return ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(funcionario));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Atualiza os dados cadastrais de um funcionário existente. O id vem exclusivamente da URL.
     * Se senha ou ativo não forem informados, os valores atuais do registro são preservados.
     * @param id identificador do funcionário vindo da URL
     * @param dto novos dados cadastrais do funcionário
     * @return 200 OK com os dados públicos atualizados (sem senha), 400 se inválido ou 404 se inexistente.
     */
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuncionarioResponseDTO> update(@PathVariable("id") Long id,
                                                         @Valid @RequestBody FuncionarioUpdateRequestDTO dto) {
        log.info("Requisição para atualizar funcionário ID: {}", id);

        Funcionario existente = funcionarioService.findById(id);
        existente.setNome(dto.nome());
        existente.setUsername(dto.username());
        existente.setPerfil(dto.perfil());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            existente.setSenha(dto.senha());
        }
        if (dto.ativo() != null) {
            existente.setAtivo(dto.ativo());
        }

        Funcionario atualizado = funcionarioService.update(existente);
        return ResponseEntity.status(HttpStatus.OK).body(FuncionarioResponseDTO.fromEntity(atualizado));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Inativa logicamente um funcionário (ativo = false), preservando o histórico operacional (DOM-05).
     * @param id identificador do funcionário vindo da URL
     * @return 204 No Content em caso de sucesso, ou 404 Not Found se o id não existir.
     */
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("Requisição para inativar funcionário ID: {}", id);
        funcionarioService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private List<FuncionarioResponseDTO> toResponseList(List<Funcionario> funcionarios) {
        return funcionarios.stream().map(FuncionarioResponseDTO::fromEntity).toList();
    }
}
