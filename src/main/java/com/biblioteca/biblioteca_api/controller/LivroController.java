package com.biblioteca.biblioteca_api.controller;

import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping
    public ResponseEntity<Page<LivroDTO>> listarTodos(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Integer anoPublicacao,
            @RequestParam(required = false) Long autorId,
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable) {

        return ResponseEntity.ok(livroService.buscarComFiltros(categoriaId, anoPublicacao, autorId, pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LivroDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<LivroDTO> criar(@Valid @RequestBody LivroDTO livroDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.criar(livroDTO));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<LivroDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LivroDTO livroDTO) {
        return ResponseEntity.ok(livroService.atualizar(id, livroDTO));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/buscar")
    public ResponseEntity<List<LivroDTO>> buscarPorTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(livroService.buscarPorTitulo(titulo));
    }

    @GetMapping("/buscar/paginado")
    public ResponseEntity<Page<LivroDTO>> buscarPorTituloPaginado(
            @RequestParam String titulo,
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable) {
        return ResponseEntity.ok(livroService.buscarPorTitulo(titulo, pageable));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<LivroDTO> buscarPorIsbn(@PathVariable String isbn) {
        Optional<LivroDTO> livro = livroService.buscarPorIsbn(isbn);
        if (livro.isPresent()) {
            return ResponseEntity.ok(livro.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/verificar-isbn")
    public ResponseEntity<Boolean> verificarIsbnExiste(@RequestParam String isbn) {
        return ResponseEntity.ok(livroService.existePorIsbn(isbn));
    }
}