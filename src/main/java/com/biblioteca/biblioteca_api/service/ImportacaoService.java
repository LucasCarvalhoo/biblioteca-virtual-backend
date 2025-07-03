package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.ImportacaoDTO;
import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.model.Autor;
import com.biblioteca.biblioteca_api.model.Categoria;
import com.biblioteca.biblioteca_api.repository.AutorRepository;
import com.biblioteca.biblioteca_api.repository.CategoriaRepository;
import com.biblioteca.biblioteca_api.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class ImportacaoService {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoService.class);

    @Autowired
    private LivroScrapingService scrapingService;

    @Autowired
    private LivroService livroService;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LivroRepository livroRepository;

    public ImportacaoDTO importarLivro(ImportacaoDTO importacaoDTO) {
        logger.info("Iniciando importação de livro da URL: {}", importacaoDTO.getUrl());

        try {
            validarRelacionamentos(importacaoDTO.getAutorId(), importacaoDTO.getCategoriaId());

            LivroDTO livroExtraido = scrapingService.extrairDadosLivro(importacaoDTO.getUrl());

            livroExtraido.setAutorId(importacaoDTO.getAutorId());
            livroExtraido.setCategoriaId(importacaoDTO.getCategoriaId());

            if (livroRepository.existsByIsbn(livroExtraido.getIsbn())) {
                importacaoDTO.setStatus("ERRO");
                importacaoDTO.setMensagem("Livro já existe na biblioteca com ISBN: " + livroExtraido.getIsbn());
                logger.warn("Tentativa de importar livro duplicado: {}", livroExtraido.getIsbn());
                return importacaoDTO;
            }

            LivroDTO livroSalvo = livroService.criar(livroExtraido);

            importacaoDTO.setLivroImportado(livroSalvo);
            importacaoDTO.setStatus("SUCESSO");
            importacaoDTO.setMensagem("Livro importado com sucesso: " + livroSalvo.getTitulo());

            logger.info("Livro importado com sucesso: {} - ISBN: {}", livroSalvo.getTitulo(), livroSalvo.getIsbn());

            return importacaoDTO;

        } catch (EntityNotFoundException e) {
            importacaoDTO.setStatus("ERRO");
            importacaoDTO.setMensagem("Erro de relacionamento: " + e.getMessage());
            logger.error("Erro de relacionamento na importação: {}", e.getMessage());
            return importacaoDTO;

        } catch (Exception e) {
            importacaoDTO.setStatus("ERRO");
            importacaoDTO.setMensagem("Erro na importação: " + e.getMessage());
            logger.error("Erro na importação de livro da URL: {}", importacaoDTO.getUrl(), e);
            return importacaoDTO;
        }
    }

    private void validarRelacionamentos(Long autorId, Long categoriaId) {
        Optional<Autor> autor = autorRepository.findById(autorId);
        if (autor.isEmpty()) {
            throw new EntityNotFoundException("Autor não encontrado com ID: " + autorId);
        }

        Optional<Categoria> categoria = categoriaRepository.findById(categoriaId);
        if (categoria.isEmpty()) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + categoriaId);
        }
    }
}