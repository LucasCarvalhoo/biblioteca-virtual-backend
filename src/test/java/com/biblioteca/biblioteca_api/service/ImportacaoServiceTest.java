package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.ImportacaoDTO;
import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.model.Autor;
import com.biblioteca.biblioteca_api.model.Categoria;
import com.biblioteca.biblioteca_api.repository.AutorRepository;
import com.biblioteca.biblioteca_api.repository.CategoriaRepository;
import com.biblioteca.biblioteca_api.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TESTES UNITÁRIOS DA CLASSE - ImportacaoServiceTest")
public class ImportacaoServiceTest {

    @Mock
    private LivroScrapingService scrapingService;

    @Mock
    private LivroService livroService;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private ImportacaoService importacaoService;

    private ImportacaoDTO criarImportacaoDTO() {
        ImportacaoDTO dto = new ImportacaoDTO();
        dto.setUrl("https://www.amazon.com.br/teste");
        dto.setAutorId(1L);
        dto.setCategoriaId(1L);
        return dto;
    }

    private LivroDTO criarLivroDTO() {
        LivroDTO dto = new LivroDTO();
        dto.setId(1L);
        dto.setTitulo("Livro Teste");
        dto.setIsbn("9781234567890");
        dto.setAnoPublicacao(2023);
        dto.setPreco(BigDecimal.valueOf(39.90));
        dto.setAutorId(1L);
        dto.setCategoriaId(1L);
        return dto;
    }

    private Autor criarAutor() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Autor Teste");
        autor.setEmail("autor@teste.com");
        autor.setDataNascimento(LocalDate.of(1980, 1, 1));
        return autor;
    }

    private Categoria criarCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");
        categoria.setDescricao("Descrição teste");
        return categoria;
    }

    @Test
    @DisplayName("importarLivro deve importar com sucesso quando dados são válidos")
    void importarLivro_quandoDadosValidos_entaoImportaComSucesso() {
        ImportacaoDTO importacaoDTO = criarImportacaoDTO();
        LivroDTO livroExtraido = criarLivroDTO();
        LivroDTO livroSalvo = criarLivroDTO();
        Autor autor = criarAutor();
        Categoria categoria = criarCategoria();

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(scrapingService.extrairDadosLivro(anyString())).thenReturn(livroExtraido);
        when(livroRepository.existsByIsbn(anyString())).thenReturn(false);
        when(livroService.criar(any(LivroDTO.class))).thenReturn(livroSalvo);

        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);

        assertEquals("SUCESSO", resultado.getStatus());
        assertEquals("Livro importado com sucesso: Livro Teste", resultado.getMensagem());
        assertEquals(livroSalvo, resultado.getLivroImportado());

        verify(scrapingService).extrairDadosLivro(importacaoDTO.getUrl());
        verify(livroService).criar(any(LivroDTO.class));
    }

    @Test
    @DisplayName("importarLivro deve retornar erro quando autor não existe")
    void importarLivro_quandoAutorInexistente_entaoRetornaErro() {
        ImportacaoDTO importacaoDTO = criarImportacaoDTO();

        when(autorRepository.findById(1L)).thenReturn(Optional.empty());

        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);

        assertEquals("ERRO", resultado.getStatus());
        assertEquals("Erro de relacionamento: Autor não encontrado com ID: 1", resultado.getMensagem());
        assertNull(resultado.getLivroImportado());

        verify(scrapingService, never()).extrairDadosLivro(anyString());
        verify(livroService, never()).criar(any(LivroDTO.class));
    }

    @Test
    @DisplayName("importarLivro deve retornar erro quando categoria não existe")
    void importarLivro_quandoCategoriaInexistente_entaoRetornaErro() {
        ImportacaoDTO importacaoDTO = criarImportacaoDTO();
        Autor autor = criarAutor();

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);

        assertEquals("ERRO", resultado.getStatus());
        assertEquals("Erro de relacionamento: Categoria não encontrada com ID: 1", resultado.getMensagem());
        assertNull(resultado.getLivroImportado());

        verify(scrapingService, never()).extrairDadosLivro(anyString());
        verify(livroService, never()).criar(any(LivroDTO.class));
    }

    @Test
    @DisplayName("importarLivro deve retornar erro quando ISBN já existe")
    void importarLivro_quandoIsbnJaExiste_entaoRetornaErro() {
        ImportacaoDTO importacaoDTO = criarImportacaoDTO();
        LivroDTO livroExtraido = criarLivroDTO();
        Autor autor = criarAutor();
        Categoria categoria = criarCategoria();

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(scrapingService.extrairDadosLivro(anyString())).thenReturn(livroExtraido);
        when(livroRepository.existsByIsbn(anyString())).thenReturn(true);

        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);

        assertEquals("ERRO", resultado.getStatus());
        assertEquals("Livro já existe na biblioteca com ISBN: 9781234567890", resultado.getMensagem());
        assertNull(resultado.getLivroImportado());

        verify(scrapingService).extrairDadosLivro(importacaoDTO.getUrl());
        verify(livroService, never()).criar(any(LivroDTO.class));
    }

    @Test
    @DisplayName("importarLivro deve retornar erro quando scraping falha")
    void importarLivro_quandoScrapingFalha_entaoRetornaErro() {
        ImportacaoDTO importacaoDTO = criarImportacaoDTO();
        Autor autor = criarAutor();
        Categoria categoria = criarCategoria();

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(scrapingService.extrairDadosLivro(anyString())).thenThrow(new RuntimeException("Erro de scraping"));

        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);

        assertEquals("ERRO", resultado.getStatus());
        assertEquals("Erro na importação: Erro de scraping", resultado.getMensagem());
        assertNull(resultado.getLivroImportado());

        verify(scrapingService).extrairDadosLivro(importacaoDTO.getUrl());
        verify(livroService, never()).criar(any(LivroDTO.class));
    }

    @Test
    @DisplayName("importarLivro deve retornar erro quando criação do livro falha")
    void importarLivro_quandoCriacaoLivroFalha_entaoRetornaErro() {
        ImportacaoDTO importacaoDTO = criarImportacaoDTO();
        LivroDTO livroExtraido = criarLivroDTO();
        Autor autor = criarAutor();
        Categoria categoria = criarCategoria();

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(scrapingService.extrairDadosLivro(anyString())).thenReturn(livroExtraido);
        when(livroRepository.existsByIsbn(anyString())).thenReturn(false);
        when(livroService.criar(any(LivroDTO.class))).thenThrow(new RuntimeException("Erro ao salvar livro"));

        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);

        assertEquals("ERRO", resultado.getStatus());
        assertEquals("Erro na importação: Erro ao salvar livro", resultado.getMensagem());
        assertNull(resultado.getLivroImportado());

        verify(scrapingService).extrairDadosLivro(importacaoDTO.getUrl());
        verify(livroService).criar(any(LivroDTO.class));
    }

    @Test
    @DisplayName("importarLivro deve configurar autorId e categoriaId no livro extraído")
    void importarLivro_quandoSucesso_entaoConfiguraRelacionamentos() {
        ImportacaoDTO importacaoDTO = criarImportacaoDTO();
        LivroDTO livroExtraido = criarLivroDTO();
        livroExtraido.setAutorId(null);
        livroExtraido.setCategoriaId(null);

        LivroDTO livroSalvo = criarLivroDTO();
        Autor autor = criarAutor();
        Categoria categoria = criarCategoria();

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(scrapingService.extrairDadosLivro(anyString())).thenReturn(livroExtraido);
        when(livroRepository.existsByIsbn(anyString())).thenReturn(false);
        when(livroService.criar(any(LivroDTO.class))).thenReturn(livroSalvo);

        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);

        assertEquals("SUCESSO", resultado.getStatus());

        verify(livroService).criar(argThat(livro ->
                livro.getAutorId().equals(1L) && livro.getCategoriaId().equals(1L)
        ));
    }

    @Test
    @DisplayName("importarLivro deve tratar EntityNotFoundException corretamente")
    void importarLivro_quandoEntityNotFound_entaoTrataCorreto() {
        ImportacaoDTO importacaoDTO = criarImportacaoDTO();
        Autor autor = criarAutor();
        Categoria categoria = criarCategoria();

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(scrapingService.extrairDadosLivro(anyString())).thenReturn(criarLivroDTO());
        when(livroRepository.existsByIsbn(anyString())).thenReturn(false);
        when(livroService.criar(any(LivroDTO.class))).thenThrow(new EntityNotFoundException("Erro de entidade"));

        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);

        assertEquals("ERRO", resultado.getStatus());
        assertEquals("Erro de relacionamento: Erro de entidade", resultado.getMensagem());
        assertNull(resultado.getLivroImportado());
    }

    @Test
    @DisplayName("importarLivro deve preservar URL original no resultado")
    void importarLivro_quandoChamado_entaoPreservaUrl() {
        ImportacaoDTO importacaoDTO = criarImportacaoDTO();
        String urlOriginal = importacaoDTO.getUrl();

        when(autorRepository.findById(1L)).thenReturn(Optional.empty());

        ImportacaoDTO resultado = importacaoService.importarLivro(importacaoDTO);

        assertEquals(urlOriginal, resultado.getUrl());
        assertEquals(1L, resultado.getAutorId());
        assertEquals(1L, resultado.getCategoriaId());
    }
}