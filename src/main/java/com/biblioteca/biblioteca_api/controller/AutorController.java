package com.biblioteca.biblioteca_api.controller;

import com.biblioteca.biblioteca_api.dto.AutorDTO;
import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.service.AutorService;
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
@RequestMapping("/autor")
public class AutorController {

    @Autowired
    private AutorService autorService;

    @GetMapping
    public ResponseEntity<Page<AutorDTO>> listarTodos(@PageableDefault(size = 10,sort = "nome") Pageable pageable){
        return ResponseEntity.ok(autorService.listarTodos(pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AutorDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(autorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AutorDTO> criar(@RequestBody AutorDTO autorDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.criar(autorDTO));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AutorDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AutorDTO autorDTO){
        return ResponseEntity.ok(autorService.atualizar(id, autorDTO));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        autorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/livros")
    public ResponseEntity<List<LivroDTO>> listarLivrosDoAutor(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.listarLivrosDoAutor(id));
    }
}
