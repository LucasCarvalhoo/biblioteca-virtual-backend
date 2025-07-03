package com.biblioteca.biblioteca_api.controller;

import com.biblioteca.biblioteca_api.dto.ImportacaoDTO;
import com.biblioteca.biblioteca_api.service.ImportacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class ImportacaoController {

    @Autowired
    private ImportacaoService importacaoService;

    @PostMapping("/importar")
    public ResponseEntity<ImportacaoDTO> importarLivro(@Valid @RequestBody ImportacaoDTO importacaoDTO) {
        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);
        return ResponseEntity.ok(resultado);
    }
}