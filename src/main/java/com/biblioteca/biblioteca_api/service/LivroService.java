package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.AutorDTO;
import com.biblioteca.biblioteca_api.dto.CategoriaDTO;
import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.model.Autor;
import com.biblioteca.biblioteca_api.model.Categoria;
import com.biblioteca.biblioteca_api.model.Livro;
import com.biblioteca.biblioteca_api.repository.AutorRepository;
import com.biblioteca.biblioteca_api.repository.CategoriaRepository;
import com.biblioteca.biblioteca_api.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Page<LivroDTO> listarTodos(Pageable pageable) {
        return livroRepository.findAll(pageable)
                .map(this::converterParaDTO);
    }

    public LivroDTO buscarPorId(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com ID: " + id));
        return converterParaDTO(livro);
    }

    public LivroDTO criar(LivroDTO livroDTO) {
        validarIsbnUnico(livroDTO.getIsbn(), null);
        validarRelacionamentos(livroDTO.getAutorId(), livroDTO.getCategoriaId());

        Livro livro = converterParaEntidade(livroDTO);
        Livro livroSalvo = livroRepository.save(livro);
        return converterParaDTO(livroSalvo);
    }

    public LivroDTO atualizar(Long id, LivroDTO livroDTO) {
        Livro livroExistente = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com ID: " + id));

        validarIsbnUnico(livroDTO.getIsbn(), id);
        validarRelacionamentos(livroDTO.getAutorId(), livroDTO.getCategoriaId());

        atualizarCampos(livroExistente, livroDTO);

        Livro livroAtualizado = livroRepository.save(livroExistente);
        return converterParaDTO(livroAtualizado);
    }

    public void deletar(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com ID: " + id));

        livroRepository.delete(livro);
    }

    public Page<LivroDTO> buscarComFiltros(Long categoriaId, Integer anoPublicacao, Long autorId, Pageable pageable) {
        return livroRepository.buscarComFiltros(categoriaId, anoPublicacao, autorId, pageable)
                .map(this::converterParaDTO);
    }

    public List<LivroDTO> buscarPorTitulo(String titulo) {
        List<Livro> livros = livroRepository.findByTituloContainingIgnoreCase(titulo);
        return livros.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public Page<LivroDTO> buscarPorTitulo(String titulo, Pageable pageable) {
        return livroRepository.findByTituloContainingIgnoreCase(titulo, pageable)
                .map(this::converterParaDTO);
    }

    public Optional<LivroDTO> buscarPorIsbn(String isbn) {
        return livroRepository.findByIsbn(isbn)
                .map(this::converterParaDTO);
    }

    public boolean existePorIsbn(String isbn) {
        return livroRepository.existsByIsbn(isbn);
    }

    private void validarIsbnUnico(String isbn, Long id) {
        Optional<Livro> livroExistente = livroRepository.findByIsbn(isbn);
        if (livroExistente.isPresent() && !livroExistente.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe um livro com este ISBN: " + isbn);
        }
    }

    private void validarRelacionamentos(Long autorId, Long categoriaId) {
        if (autorRepository.findById(autorId).isEmpty()) {
            throw new EntityNotFoundException("Autor não encontrado com ID: " + autorId);
        }

        if (categoriaRepository.findById(categoriaId).isEmpty()) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + categoriaId);
        }
    }

    private void atualizarCampos(Livro livroExistente, LivroDTO livroDTO) {
        livroExistente.setTitulo(livroDTO.getTitulo());
        livroExistente.setIsbn(livroDTO.getIsbn());
        livroExistente.setAnoPublicacao(livroDTO.getAnoPublicacao());
        livroExistente.setPreco(livroDTO.getPreco());
        livroExistente.setUrlOrigem(livroDTO.getUrlOrigem());

        if (!livroExistente.getAutor().getId().equals(livroDTO.getAutorId())) {
            Autor novoAutor = autorRepository.findById(livroDTO.getAutorId())
                    .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado"));
            livroExistente.setAutor(novoAutor);
        }

        if (!livroExistente.getCategoria().getId().equals(livroDTO.getCategoriaId())) {
            Categoria novaCategoria = categoriaRepository.findById(livroDTO.getCategoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
            livroExistente.setCategoria(novaCategoria);
        }
    }

    LivroDTO converterParaDTO(Livro livro) {
        LivroDTO dto = new LivroDTO();
        dto.setId(livro.getId());
        dto.setTitulo(livro.getTitulo());
        dto.setIsbn(livro.getIsbn());
        dto.setAnoPublicacao(livro.getAnoPublicacao());
        dto.setPreco(livro.getPreco());
        dto.setAutorId(livro.getAutor().getId());
        dto.setCategoriaId(livro.getCategoria().getId());
        dto.setUrlOrigem(livro.getUrlOrigem());
        dto.setDataCadastro(livro.getDataCadastro());
        dto.setDataAtualizacao(livro.getDataAtualizacao());

        AutorDTO autorDTO = new AutorDTO();
        autorDTO.setId(livro.getAutor().getId());
        autorDTO.setNome(livro.getAutor().getNome());
        autorDTO.setEmail(livro.getAutor().getEmail());
        autorDTO.setDataNascimento(livro.getAutor().getDataNascimento());
        dto.setAutor(autorDTO);

        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(livro.getCategoria().getId());
        categoriaDTO.setNome(livro.getCategoria().getNome());
        categoriaDTO.setDescricao(livro.getCategoria().getDescricao());
        dto.setCategoria(categoriaDTO);

        return dto;
    }

    private Livro converterParaEntidade(LivroDTO dto) {
        Livro livro = new Livro();
        livro.setTitulo(dto.getTitulo());
        livro.setIsbn(dto.getIsbn());
        livro.setAnoPublicacao(dto.getAnoPublicacao());
        livro.setPreco(dto.getPreco());
        livro.setUrlOrigem(dto.getUrlOrigem());

        Autor autor = autorRepository.findById(dto.getAutorId())
                .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado"));
        livro.setAutor(autor);

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
        livro.setCategoria(categoria);

        return livro;
    }
}