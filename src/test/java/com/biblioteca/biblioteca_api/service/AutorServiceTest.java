package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.AutorDTO;
import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.model.Autor;
import com.biblioteca.biblioteca_api.model.Livro;
import com.biblioteca.biblioteca_api.repository.AutorRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TESTES UNITÁRIOS DA CLASSE - AutorServiceTest")
public class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private AutorService autorService;

    @Test
    @DisplayName("listarTodos deve retornar autores paginados quando a página é válida")
    void listarTodos_quandoPaginaValida_entaoRetornaAutoresPaginados() {
        Autor autor1 = new Autor();
        autor1.setId(1L);
        autor1.setNome("Akira Toriyama");
        autor1.setEmail("akira@gmail.com");
        autor1.setDataNascimento(LocalDate.of(1955, 4, 5));
        autor1.setLivros(new ArrayList<>());

        Autor autor2 = new Autor();
        autor2.setId(2L);
        autor2.setNome("Tite Kubo");
        autor2.setEmail("tite@gmail.com");
        autor2.setDataNascimento(LocalDate.of(1977, 6, 26));
        autor2.setLivros(new ArrayList<>());

        List<Autor> autores = List.of(autor1, autor2);
        Page<Autor> page = new PageImpl<>(autores);
        Pageable pageable = PageRequest.of(0, 10);

        when(autorRepository.findAll(pageable)).thenReturn(page);

        Page<AutorDTO> resultado = autorService.listarTodos(pageable);

        assertEquals(2, resultado.getTotalElements());
        assertEquals("Akira Toriyama", resultado.getContent().get(0).getNome());
        assertEquals("Tite Kubo", resultado.getContent().get(1).getNome());
    }

    @Test
    @DisplayName("buscarPorId deve retornar o autor quando o ID é válido")
    void buscarPorId_quandoIdValido_entaoRetornaAutor() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Akira Toriyama");
        autor.setEmail("akira@gmail.com");
        autor.setDataNascimento(LocalDate.of(1955, 4, 5));
        autor.setLivros(new ArrayList<>());

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

        AutorDTO resultado = autorService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Akira Toriyama", resultado.getNome());
        assertEquals("akira@gmail.com", resultado.getEmail());
    }

    @Test
    @DisplayName("buscarPorId deve lançar EntityNotFoundException quando o ID não existe")
    void buscarPorId_quandoIdInexistente_entaoLancaEntityNotFound() {
        when(autorRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> autorService.buscarPorId(999L)
        );

        assertEquals("Autor não encontrado com ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("criar deve salvar autor quando o e-mail é único")
    void criar_quandoEmailUnico_entaoSalvaAutor() {
        AutorDTO autorDTO = new AutorDTO();
        autorDTO.setNome("Masashi Kishimoto");
        autorDTO.setEmail("masashi@gmail.com");
        autorDTO.setDataNascimento(LocalDate.of(1974, 11, 8));

        Autor autorSalvo = new Autor();
        autorSalvo.setId(1L);
        autorSalvo.setNome("Masashi Kishimoto");
        autorSalvo.setEmail("masashi@gmail.com");
        autorSalvo.setDataNascimento(LocalDate.of(1974, 11, 8));
        autorSalvo.setLivros(new ArrayList<>());

        when(autorRepository.findByEmail("masashi@gmail.com")).thenReturn(Optional.empty());
        when(autorRepository.save(any(Autor.class))).thenReturn(autorSalvo);

        AutorDTO resultado = autorService.criar(autorDTO);

        assertEquals(1L, resultado.getId());
        assertEquals("Masashi Kishimoto", resultado.getNome());
        assertEquals("masashi@gmail.com", resultado.getEmail());
        verify(autorRepository).save(any(Autor.class));
    }

    @Test
    @DisplayName("criar deve lançar IllegalArgumentException quando o e-mail já está cadastrado")
    void criar_quandoEmailJaExiste_entaoLancaIllegalArgument() {
        AutorDTO autorDTO = new AutorDTO();
        autorDTO.setNome("Novo Autor");
        autorDTO.setEmail("akira@gmail.com");
        autorDTO.setDataNascimento(LocalDate.of(1980, 1, 1));

        Autor autorExistente = new Autor();
        autorExistente.setId(1L);
        autorExistente.setEmail("akira@gmail.com");

        when(autorRepository.findByEmail("akira@gmail.com")).thenReturn(Optional.of(autorExistente));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> autorService.criar(autorDTO)
        );

        assertEquals("Já existe um autor com este email: akira@gmail.com", exception.getMessage());
        verify(autorRepository, never()).save(any(Autor.class));
    }

    @Test
    @DisplayName("atualizar deve atualizar o autor quando o ID é válido")
    void atualizar_quandoIdValido_entaoAtualizaAutor() {
        Autor autorExistente = new Autor();
        autorExistente.setId(1L);
        autorExistente.setNome("Nome Antigo");
        autorExistente.setEmail("email@antigo.com");
        autorExistente.setDataNascimento(LocalDate.of(1980, 1, 1));
        autorExistente.setLivros(new ArrayList<>());

        AutorDTO autorDTO = new AutorDTO();
        autorDTO.setNome("Nome Novo");
        autorDTO.setEmail("email@novo.com");
        autorDTO.setDataNascimento(LocalDate.of(1985, 5, 5));

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autorExistente));
        when(autorRepository.findByEmail("email@novo.com")).thenReturn(Optional.empty());
        when(autorRepository.save(any(Autor.class))).thenReturn(autorExistente);

        AutorDTO resultado = autorService.atualizar(1L, autorDTO);

        assertEquals("Nome Novo", resultado.getNome());
        assertEquals("email@novo.com", resultado.getEmail());
        verify(autorRepository).save(autorExistente);
    }

    @Test
    @DisplayName("atualizar deve lançar EntityNotFoundException quando o ID não existe")
    void atualizar_quandoIdInexistente_entaoLancaEntityNotFound() {
        AutorDTO autorDTO = new AutorDTO();
        autorDTO.setNome("Nome Teste");
        autorDTO.setEmail("teste@gmail.com");
        autorDTO.setDataNascimento(LocalDate.of(1980, 1, 1));

        when(autorRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> autorService.atualizar(999L, autorDTO)
        );

        assertEquals("Autor não encontrado com ID: 999", exception.getMessage());
        verify(autorRepository, never()).save(any(Autor.class));
    }

    @Test
    @DisplayName("deletar deve remover o autor quando ele não possui livros cadastrados")
    void deletar_quandoAutorSemLivros_entaoDeletaComSucesso() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Autor Teste");
        autor.setEmail("teste@gmail.com");
        autor.setDataNascimento(LocalDate.of(1980, 1, 1));
        autor.setLivros(new ArrayList<>());

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

        assertDoesNotThrow(() -> autorService.deletar(1L));

        verify(autorRepository).delete(autor);
    }

    @Test
    @DisplayName("deletar deve lançar IllegalStateException quando o autor possui livros associados")
    void deletar_quandoAutorComLivros_entaoLancaIllegalState() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Autor com Livros");
        autor.setEmail("autor@gmail.com");
        autor.setDataNascimento(LocalDate.of(1980, 1, 1));

        Livro livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Livro Teste");
        livro.setIsbn("1234567890");
        livro.setAnoPublicacao(2020);
        livro.setPreco(BigDecimal.valueOf(29.90));
        livro.setDataCadastro(LocalDateTime.now());
        livro.setDataAtualizacao(LocalDateTime.now());

        List<Livro> livros = List.of(livro);
        autor.setLivros(livros);

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> autorService.deletar(1L)
        );

        assertEquals("Não é possível deletar autor que possui livros cadastrados", exception.getMessage());
        verify(autorRepository, never()).delete(any(Autor.class));
    }

    @Test
    @DisplayName("listarLivrosDoAutor deve retornar os livros do autor quando ele existe")
    void listarLivrosDoAutor_quandoAutorExiste_entaoRetornaLivros() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Autor Teste");
        autor.setEmail("autor@gmail.com");
        autor.setDataNascimento(LocalDate.of(1980, 1, 1));

        Livro livro1 = new Livro();
        livro1.setId(1L);
        livro1.setTitulo("Livro 1");
        livro1.setIsbn("1111111111");
        livro1.setAnoPublicacao(2020);
        livro1.setPreco(BigDecimal.valueOf(25.90));
        livro1.setDataCadastro(LocalDateTime.now());
        livro1.setDataAtualizacao(LocalDateTime.now());

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setTitulo("Livro 2");
        livro2.setIsbn("2222222222");
        livro2.setAnoPublicacao(2021);
        livro2.setPreco(BigDecimal.valueOf(35.90));
        livro2.setDataCadastro(LocalDateTime.now());
        livro2.setDataAtualizacao(LocalDateTime.now());

        List<Livro> livros = List.of(livro1, livro2);
        autor.setLivros(livros);

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

        List<LivroDTO> resultado = autorService.listarLivrosDoAutor(1L);

        assertEquals(2, resultado.size());
        assertEquals("Livro 1", resultado.get(0).getTitulo());
        assertEquals("Livro 2", resultado.get(1).getTitulo());
    }

    @Test
    @DisplayName("listarLivrosDoAutor deve lançar EntityNotFoundException quando o autor não existe")
    void listarLivrosDoAutor_quandoAutorInexistente_entaoLancaEntityNotFound() {
        when(autorRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> autorService.listarLivrosDoAutor(999L)
        );

        assertEquals("Autor não encontrado com ID: 999", exception.getMessage());
    }
}