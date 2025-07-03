package com.biblioteca.biblioteca_api.controller;

import com.biblioteca.biblioteca_api.dto.CategoriaDTO;
import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.model.Categoria;
import com.biblioteca.biblioteca_api.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<Page<CategoriaDTO>> listarTodos(@PageableDefault(size = 10,sort = "nome") Pageable pageable){
        return ResponseEntity.ok(categoriaService.listarTodos(pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @GetMapping(value = "/buscar")
    public ResponseEntity<Page<CategoriaDTO>> BuscarPorNome(@RequestParam String nome, @PageableDefault(size = 10,sort = "nome") Pageable pageable){
        return ResponseEntity.ok(categoriaService.buscarPorNome(nome, pageable));
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> criar(@RequestBody @Valid CategoriaDTO categoriaDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(categoriaDTO));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(@PathVariable Long id,
                                                  @RequestBody @Valid CategoriaDTO categoriaDTO){
        return ResponseEntity.ok(categoriaService.atualizar(id, categoriaDTO));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/livros")
    public ResponseEntity<List<LivroDTO>> listarLivrosDaCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.listarLivrosDaCategoria(id));
    }
}
