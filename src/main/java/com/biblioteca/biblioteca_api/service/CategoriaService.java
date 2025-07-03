package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.CategoriaDTO;
import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.model.Categoria;
import com.biblioteca.biblioteca_api.model.Livro;
import com.biblioteca.biblioteca_api.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Page<CategoriaDTO> listarTodos(Pageable pageable) {
        return categoriaRepository.findAll(pageable)
                .map(this::converterParaDTO);
    }

    public CategoriaDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + id));
        return converterParaDTO(categoria);
    }

    public CategoriaDTO criar(CategoriaDTO categoriaDTO) {
        Categoria categoria = converterParaEntidade(categoriaDTO);
        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return converterParaDTO(categoriaSalva);
    }

    public CategoriaDTO atualizar(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + id));

        categoriaExistente.setNome(categoriaDTO.getNome());
        categoriaExistente.setDescricao(categoriaDTO.getDescricao());

        Categoria categoriaAtualizada = categoriaRepository.save(categoriaExistente);
        return converterParaDTO(categoriaAtualizada);
    }

    public void deletar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + id));

        if (!categoria.getLivros().isEmpty()) {
            throw new IllegalStateException("Não é possível deletar categoria que possui livros cadastrados");
        }

        categoriaRepository.delete(categoria);
    }

    public List<LivroDTO> listarLivrosDaCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com ID: " + categoriaId));
        return categoria.getLivros().stream().map(this::converterLivroParaDTO).toList();
    }

    public Page<CategoriaDTO> buscarPorNome(String nome, Pageable pageable) {
        return categoriaRepository.buscarPorNomeIgnorandoCase(nome, pageable)
                .map(this::converterParaDTO);
    }

    private CategoriaDTO converterParaDTO(Categoria categoria) {
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(categoria.getId());
        categoriaDTO.setNome(categoria.getNome());
        categoriaDTO.setDescricao(categoria.getDescricao());
        try {
            categoriaDTO.setTotalLivros(categoria.getLivros() != null ? categoria.getLivros().size() : 0);
        } catch (Exception e) {
            categoriaDTO.setTotalLivros(0);
        }
        return categoriaDTO;
    }

    private Categoria converterParaEntidade(CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria();
        categoria.setNome(categoriaDTO.getNome());
        categoria.setDescricao(categoriaDTO.getDescricao());
        return categoria;
    }

    private LivroDTO converterLivroParaDTO(Livro livro) {
        LivroDTO livroDTO = new LivroDTO();
        livroDTO.setId(livro.getId());
        livroDTO.setTitulo(livro.getTitulo());
        livroDTO.setIsbn(livro.getIsbn());
        livroDTO.setAnoPublicacao(livro.getAnoPublicacao());
        livroDTO.setPreco(livro.getPreco());
        livroDTO.setUrlOrigem(livro.getUrlOrigem());
        livroDTO.setDataCadastro(livro.getDataCadastro());
        livroDTO.setDataAtualizacao(livro.getDataAtualizacao());
        return livroDTO;
    }
}