package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.model.Autor;
import com.biblioteca.biblioteca_api.model.Categoria;
import com.biblioteca.biblioteca_api.model.Livro;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TESTES UNITÁRIOS DA CLASSE - LivroServiceTest")
public class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private LivroService livroService;

    private Livro gerarLivro() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Autor Teste");
        autor.setEmail("autor@teste.com");
        autor.setDataNascimento(LocalDate.of(1980, 1, 1));

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");
        categoria.setDescricao("Descrição da categoria");

        Livro livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Livro Teste");
        livro.setIsbn("1234567890123");
        livro.setAnoPublicacao(2022);
        livro.setPreco(BigDecimal.valueOf(39.90));
        livro.setUrlOrigem("http://teste.com/livro");
        livro.setDataCadastro(LocalDateTime.now());
        livro.setDataAtualizacao(LocalDateTime.now());
        livro.setAutor(autor);
        livro.setCategoria(categoria);

        return livro;
    }

    @Test
    @DisplayName("listarTodos deve retornar livros paginados")
    void listarTodos_deveRetornarLivrosPaginados() {
        Livro livro = gerarLivro();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Livro> page = new PageImpl<>(List.of(livro));

        when(livroRepository.findAll(pageable)).thenReturn(page);

        Page<LivroDTO> resultado = livroService.listarTodos(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Livro Teste", resultado.getContent().get(0).getTitulo());
    }

    @Test
    @DisplayName("buscarPorId deve retornar livro quando ID válido")
    void buscarPorId_quandoIdValido_entaoRetornaLivro() {
        Livro livro = gerarLivro();
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        LivroDTO resultado = livroService.buscarPorId(1L);

        assertEquals("Livro Teste", resultado.getTitulo());
        assertEquals("1234567890123", resultado.getIsbn());
    }

    @Test
    @DisplayName("buscarPorId deve lançar EntityNotFoundException quando ID inválido")
    void buscarPorId_quandoIdInvalido_entaoLancaEntityNotFound() {
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> livroService.buscarPorId(99L));

        assertEquals("Livro não encontrado com ID: 99", exception.getMessage());
    }

    @Test
    @DisplayName("criar deve salvar livro quando ISBN é único e relações válidas")
    void criar_quandoIsbnUnicoERelacoesValidas_entaoSalvaLivro() {
        Livro livro = gerarLivro();
        LivroDTO dto = livroService.converterParaDTO(livro);

        when(livroRepository.findByIsbn(dto.getIsbn())).thenReturn(Optional.empty());
        when(autorRepository.findById(dto.getAutorId())).thenReturn(Optional.of(livro.getAutor()));
        when(categoriaRepository.findById(dto.getCategoriaId())).thenReturn(Optional.of(livro.getCategoria()));
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        LivroDTO resultado = livroService.criar(dto);

        assertEquals(dto.getTitulo(), resultado.getTitulo());
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    @DisplayName("criar deve lançar IllegalArgumentException quando ISBN já existe")
    void criar_quandoIsbnExistente_entaoLancaExcecao() {
        Livro livro = gerarLivro();
        LivroDTO dto = livroService.converterParaDTO(livro);

        when(livroRepository.findByIsbn(dto.getIsbn())).thenReturn(Optional.of(livro));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> livroService.criar(dto));

        assertEquals("Já existe um livro com este ISBN: 1234567890123", exception.getMessage());
    }

    @Test
    @DisplayName("deletar deve remover livro quando ID válido")
    void deletar_quandoIdValido_entaoRemoveLivro() {
        Livro livro = gerarLivro();
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        assertDoesNotThrow(() -> livroService.deletar(1L));
        verify(livroRepository).delete(livro);
    }

    @Test
    @DisplayName("deletar deve lançar EntityNotFoundException quando ID inválido")
    void deletar_quandoIdInvalido_entaoLancaExcecao() {
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> livroService.deletar(99L));

        assertEquals("Livro não encontrado com ID: 99", exception.getMessage());
    }
}
