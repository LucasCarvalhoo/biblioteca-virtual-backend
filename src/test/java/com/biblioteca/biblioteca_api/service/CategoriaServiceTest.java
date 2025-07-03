package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.CategoriaDTO;
import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.model.Categoria;
import com.biblioteca.biblioteca_api.model.Livro;
import com.biblioteca.biblioteca_api.repository.CategoriaRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TESTES UNITÁRIOS DA CLASSE - CategoriaServiceTest")
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    @DisplayName("listarTodos deve retornar categorias paginadas quando a página é válida")
    void listarTodos_quandoPaginaValida_entaoRetornaCategoriasPaginadas() {
        Categoria cat1 = new Categoria();
        cat1.setId(1L);
        cat1.setNome("Ficção");
        cat1.setDescricao("Livros de ficção");
        cat1.setLivros(new ArrayList<>());

        Categoria cat2 = new Categoria();
        cat2.setId(2L);
        cat2.setNome("Aventura");
        cat2.setDescricao("Livros de aventura");
        cat2.setLivros(new ArrayList<>());

        Page<Categoria> page = new PageImpl<>(List.of(cat1, cat2));
        Pageable pageable = PageRequest.of(0, 10);

        when(categoriaRepository.findAll(pageable)).thenReturn(page);

        Page<CategoriaDTO> resultado = categoriaService.listarTodos(pageable);

        assertEquals(2, resultado.getTotalElements());
        assertEquals("Ficção", resultado.getContent().get(0).getNome());
        assertEquals("Aventura", resultado.getContent().get(1).getNome());
    }

    @Test
    @DisplayName("buscarPorId deve retornar a categoria quando o ID é válido")
    void buscarPorId_quandoIdValido_entaoRetornaCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Ficção");
        categoria.setDescricao("Descrição de ficção");
        categoria.setLivros(new ArrayList<>());

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaDTO resultado = categoriaService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Ficção", resultado.getNome());
    }

    @Test
    @DisplayName("buscarPorId deve lançar EntityNotFoundException quando o ID não existe")
    void buscarPorId_quandoIdInexistente_entaoLancaEntityNotFound() {
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoriaService.buscarPorId(999L)
        );

        assertEquals("Categoria não encontrada com ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("criar deve salvar a categoria corretamente")
    void criar_quandoCategoriaValida_entaoSalvaCategoria() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNome("Terror");
        dto.setDescricao("Livros assustadores");

        Categoria categoriaSalva = new Categoria();
        categoriaSalva.setId(1L);
        categoriaSalva.setNome("Terror");
        categoriaSalva.setDescricao("Livros assustadores");
        categoriaSalva.setLivros(new ArrayList<>());

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);

        CategoriaDTO resultado = categoriaService.criar(dto);

        assertEquals(1L, resultado.getId());
        assertEquals("Terror", resultado.getNome());
    }

    @Test
    @DisplayName("atualizar deve atualizar a categoria quando o ID é válido")
    void atualizar_quandoIdValido_entaoAtualizaCategoria() {
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(1L);
        categoriaExistente.setNome("Drama");
        categoriaExistente.setDescricao("Drama antigo");
        categoriaExistente.setLivros(new ArrayList<>());

        CategoriaDTO dto = new CategoriaDTO();
        dto.setNome("Drama Atualizado");
        dto.setDescricao("Nova descrição");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaExistente);

        CategoriaDTO resultado = categoriaService.atualizar(1L, dto);

        assertEquals("Drama Atualizado", resultado.getNome());
        assertEquals("Nova descrição", resultado.getDescricao());
    }

    @Test
    @DisplayName("atualizar deve lançar EntityNotFoundException quando o ID não existe")
    void atualizar_quandoIdInexistente_entaoLancaEntityNotFound() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNome("Categoria Teste");
        dto.setDescricao("Teste descrição");

        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoriaService.atualizar(999L, dto)
        );

        assertEquals("Categoria não encontrada com ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("deletar deve remover a categoria quando ela não possui livros")
    void deletar_quandoCategoriaSemLivros_entaoDeletaComSucesso() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Teste");
        categoria.setLivros(new ArrayList<>());

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertDoesNotThrow(() -> categoriaService.deletar(1L));
        verify(categoriaRepository).delete(categoria);
    }

    @Test
    @DisplayName("deletar deve lançar IllegalStateException quando a categoria possui livros")
    void deletar_quandoCategoriaComLivros_entaoLancaIllegalState() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Teste");

        Livro livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Livro Teste");
        livro.setPreco(BigDecimal.valueOf(20));
        livro.setDataCadastro(LocalDateTime.now());
        livro.setDataAtualizacao(LocalDateTime.now());

        categoria.setLivros(List.of(livro));

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> categoriaService.deletar(1L)
        );

        assertEquals("Não é possível deletar categoria que possui livros cadastrados", exception.getMessage());
        verify(categoriaRepository, never()).delete(any(Categoria.class));
    }

    @Test
    @DisplayName("listarLivrosDaCategoria deve retornar livros da categoria quando ela existe")
    void listarLivrosDaCategoria_quandoCategoriaExiste_entaoRetornaLivros() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Livro livro1 = new Livro();
        livro1.setId(1L);
        livro1.setTitulo("Livro A");
        livro1.setPreco(BigDecimal.valueOf(10));
        livro1.setDataCadastro(LocalDateTime.now());
        livro1.setDataAtualizacao(LocalDateTime.now());

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setTitulo("Livro B");
        livro2.setPreco(BigDecimal.valueOf(15));
        livro2.setDataCadastro(LocalDateTime.now());
        livro2.setDataAtualizacao(LocalDateTime.now());

        categoria.setLivros(List.of(livro1, livro2));

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        List<LivroDTO> resultado = categoriaService.listarLivrosDaCategoria(1L);

        assertEquals(2, resultado.size());
        assertEquals("Livro A", resultado.get(0).getTitulo());
        assertEquals("Livro B", resultado.get(1).getTitulo());
    }

    @Test
    @DisplayName("listarLivrosDaCategoria deve lançar EntityNotFoundException quando a categoria não existe")
    void listarLivrosDaCategoria_quandoCategoriaInexistente_entaoLancaEntityNotFound() {
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoriaService.listarLivrosDaCategoria(999L)
        );

        assertEquals("Categoria não encontrada com ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("buscarPorNome deve retornar categorias paginadas com nome correspondente")
    void buscarPorNome_quandoNomeValido_entaoRetornaCategorias() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Suspense");
        categoria.setDescricao("Filmes de suspense");
        categoria.setLivros(new ArrayList<>());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Categoria> page = new PageImpl<>(List.of(categoria));

        when(categoriaRepository.buscarPorNomeIgnorandoCase("suspense", pageable)).thenReturn(page);

        Page<CategoriaDTO> resultado = categoriaService.buscarPorNome("suspense", pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Suspense", resultado.getContent().get(0).getNome());
    }
}
