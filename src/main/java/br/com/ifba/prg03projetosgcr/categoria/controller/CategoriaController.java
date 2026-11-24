package br.com.ifba.prg03projetosgcr.categoria.controller;

import br.com.ifba.prg03projetosgcr.categoria.dto.CategoriaRequestDTO;
import br.com.ifba.prg03projetosgcr.categoria.dto.CategoriaResponseDTO;
import br.com.ifba.prg03projetosgcr.categoria.entity.Categoria;
import br.com.ifba.prg03projetosgcr.categoria.service.CategoriaService;
import br.com.ifba.prg03projetosgcr.infrastructure.exception.RecursoNaoEncontradoException;
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
 * Controller REST do módulo de categorias.
 * Recebe e devolve DTOs: a entidade Categoria nunca é exposta diretamente na fronteira HTTP.
 *
 * @author João Luis
 */
@RestController
@RequestMapping(path = "/categorias")
@RequiredArgsConstructor
@Slf4j
public class CategoriaController {

    private final CategoriaService categoriaService;

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Cadastra uma nova categoria. Se "ativo" não for informado, a categoria nasce ativa.
     * @param dto dados da categoria (sem id)
     * @return 201 Created com a categoria criada, incluindo o id gerado.
     */
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriaResponseDTO> save(@Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("Requisição para cadastrar categoria: {}", dto.nome());

        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());
        categoria.setAtivo(dto.ativo() != null ? dto.ativo() : true);

        Categoria salva = categoriaService.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaResponseDTO.fromEntity(salva));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Atualiza uma categoria existente. O id vem da URL; a entidade é buscada no banco
     * e apenas os campos editáveis são alterados.
     * @param id identificador da categoria
     * @param dto novos dados da categoria
     * @return 200 OK com a categoria atualizada, ou 404 Not Found se o id não existir.
     */
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable("id") Long id,
                                                       @Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("Requisição para atualizar categoria ID: {}", id);

        Categoria existente = buscarOuFalhar(id);
        existente.setNome(dto.nome());
        existente.setDescricao(dto.descricao());
        if (dto.ativo() != null) {
            existente.setAtivo(dto.ativo());
        }

        Categoria atualizada = categoriaService.update(existente);
        return ResponseEntity.status(HttpStatus.OK).body(CategoriaResponseDTO.fromEntity(atualizada));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Inativa logicamente uma categoria (ativo = false). Não há exclusão física.
     * @param id identificador da categoria
     * @return 204 No Content, ou 404 Not Found se o id não existir.
     */
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("Requisição para inativar categoria ID: {}", id);
        categoriaService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Busca uma categoria pelo id.
     * @param id identificador da categoria
     * @return 200 OK com a categoria, ou 404 Not Found se o id não existir.
     */
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable("id") Long id) {
        log.debug("Requisição para buscar categoria ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(CategoriaResponseDTO.fromEntity(buscarOuFalhar(id)));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista todas as categorias cadastradas (ativas e inativas).
     * @return 200 OK com a lista de categorias.
     */
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoriaResponseDTO>> findAll() {
        log.debug("Requisição para listar todas as categorias");
        return ResponseEntity.status(HttpStatus.OK).body(toResponseList(categoriaService.findAll()));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista apenas as categorias ativas.
     * @return 200 OK com a lista de categorias ativas.
     */
    @GetMapping(path = "/ativas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoriaResponseDTO>> findAllAtivas() {
        log.debug("Requisição para listar categorias ativas");
        return ResponseEntity.status(HttpStatus.OK).body(toResponseList(categoriaService.findAllAtivas()));
    }

    // Converte o Optional do service em entidade ou lança 404 (nunca devolve null)
    private Categoria buscarOuFalhar(Long id) {
        return categoriaService.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada para o ID: " + id));
    }

    private List<CategoriaResponseDTO> toResponseList(List<Categoria> categorias) {
        return categorias.stream().map(CategoriaResponseDTO::fromEntity).toList();
    }
}
