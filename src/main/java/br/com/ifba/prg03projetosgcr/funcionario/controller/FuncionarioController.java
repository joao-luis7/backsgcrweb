package br.com.ifba.prg03projetosgcr.funcionario.controller;

import br.com.ifba.prg03projetosgcr.funcionario.dto.FuncionarioRequestDTO;
import br.com.ifba.prg03projetosgcr.funcionario.dto.FuncionarioResponseDTO;
import br.com.ifba.prg03projetosgcr.funcionario.dto.LoginRequestDTO;
import br.com.ifba.prg03projetosgcr.funcionario.entity.Funcionario;
import br.com.ifba.prg03projetosgcr.funcionario.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
