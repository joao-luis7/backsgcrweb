package br.com.ifba.prg03projetosgcr.produto.controller;

import br.com.ifba.prg03projetosgcr.categoria.entity.Categoria;
import br.com.ifba.prg03projetosgcr.categoria.service.CategoriaService;
import br.com.ifba.prg03projetosgcr.infrastructure.exception.RecursoNaoEncontradoException;
import br.com.ifba.prg03projetosgcr.produto.dto.ProdutoRequestDTO;
import br.com.ifba.prg03projetosgcr.produto.dto.ProdutoResponseDTO;
import br.com.ifba.prg03projetosgcr.produto.entity.Produto;
import br.com.ifba.prg03projetosgcr.produto.service.ProdutoService;
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
 * Controller REST do módulo de produtos.
 * Recebe e devolve DTOs para evitar exposição direta da entidade e problemas de carregamento LAZY.
 *
 * @author João Luis
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Cadastra um novo produto, associando opcionalmente uma categoria existente.
     * Se "ativo" não for informado, o produto nasce ativo.
     * @param dto dados do produto (sem id)
     * @return 201 Created com o produto cadastrado, incluindo o id gerado.
     */
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProdutoResponseDTO> save(@Valid @RequestBody ProdutoRequestDTO dto) {
        log.info("Requisição recebida para salvar produto: {}", dto.nome());

        Produto produto = new Produto();
        aplicarDados(produto, dto, true);

        Produto salvo = produtoService.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponseDTO.fromEntity(salvo));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Atualiza os dados e a categoria de um produto existente.
     * O id vem exclusivamente da URL.
     * @param id identificador do produto
     * @param dto novos dados do produto
     * @return 200 OK com o produto atualizado, ou 404 Not Found se produto ou categoria não existirem.
     */
    @PutMapping(path = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProdutoResponseDTO> update(@PathVariable("id") Long id,
                                                     @Valid @RequestBody ProdutoRequestDTO dto) {
        log.info("Requisição recebida para atualizar produto ID: {}", id);

        Produto existente = produtoService.findById(id);
        aplicarDados(existente, dto, false);

        Produto atualizado = produtoService.update(existente);
        return ResponseEntity.status(HttpStatus.OK).body(ProdutoResponseDTO.fromEntity(atualizado));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Inativa logicamente um produto (ativo = false). Não há exclusão física.
     * @param id identificador do produto
     * @return 204 No Content, ou 404 Not Found se o id não existir.
     */
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.info("Requisição recebida para deletar produto ID: {}", id);
        produtoService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista todos os produtos cadastrados (ativos e inativos) com suas categorias.
     * @return 200 OK com a lista de produtos.
     */
    @GetMapping(path = "/findall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProdutoResponseDTO>> findAll() {
        log.debug("Requisição recebida para buscar todos os produtos");
        return ResponseEntity.status(HttpStatus.OK).body(toResponseList(produtoService.findAll()));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Busca um produto pelo seu ID.
     * @param id identificador do produto
     * @return 200 OK com os dados do produto, ou 404 Not Found se não encontrado.
     */
    @GetMapping(path = "/findbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProdutoResponseDTO> findById(@PathVariable("id") Long id) {
        log.debug("Requisição recebida para buscar produto ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(ProdutoResponseDTO.fromEntity(produtoService.findById(id)));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Busca produtos por nome (parcial e ignorando maiúsculas/minúsculas).
     * @param nome termo de busca
     * @return 200 OK com a lista de produtos encontrados.
     */
    @GetMapping(path = "/findbynome", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProdutoResponseDTO>> findByNome(
            @RequestParam(name = "nome", required = false) String nome) {
        log.debug("Requisição recebida para buscar produtos pelo nome: {}", nome);
        return ResponseEntity.status(HttpStatus.OK).body(toResponseList(produtoService.findByNome(nome)));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista apenas os produtos ativos.
     * @return 200 OK com a lista de produtos ativos.
     */
    @GetMapping(path = "/ativos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProdutoResponseDTO>> findAllAtivos() {
        log.debug("Requisição recebida para buscar todos os produtos ativos");
        return ResponseEntity.status(HttpStatus.OK).body(toResponseList(produtoService.findAllAtivos()));
    }

    /**
     * @author João Luis
     * @apiNote Endpoint criado desde a versão 1.0.0
     *
     * Lista todos os produtos ativos com suas respectivas categorias carregadas.
     * @return 200 OK com a lista de produtos ativos e suas categorias.
     */
    @GetMapping(path = "/ativos-com-categoria", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProdutoResponseDTO>> findAllAtivosComCategoria() {
        log.debug("A receber requisição para buscar produtos ativos com categoria");
        return ResponseEntity.status(HttpStatus.OK)
                .body(toResponseList(produtoService.findAllAtivosComCategoria()));
    }

    private void aplicarDados(Produto produto, ProdutoRequestDTO dto, boolean criacao) {
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPrecoUnidade(dto.precoUnidade());
        produto.setPrecoCusto(dto.precoCusto());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());

        if (criacao) {
            produto.setAtivo(dto.ativo() != null ? dto.ativo() : true);
        } else if (dto.ativo() != null) {
            produto.setAtivo(dto.ativo());
        }

        if (dto.categoriaId() != null) {
            Categoria categoria = categoriaService.findById(dto.categoriaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Categoria não encontrada para o ID: " + dto.categoriaId()));
            produto.setCategoria(categoria);
        } else {
            produto.setCategoria(null);
        }
    }

    private List<ProdutoResponseDTO> toResponseList(List<Produto> produtos) {
        return produtos.stream().map(ProdutoResponseDTO::fromEntity).toList();
    }
}
