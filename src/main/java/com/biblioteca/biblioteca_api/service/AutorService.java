package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.AutorDTO;
import com.biblioteca.biblioteca_api.dto.LivroDTO;
import com.biblioteca.biblioteca_api.model.Autor;
import com.biblioteca.biblioteca_api.model.Livro;
import com.biblioteca.biblioteca_api.repository.AutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public Page<AutorDTO> ListarTodos(Pageable pageable){
        Page<Autor> autores = autorRepository.findAll(pageable);
        return autores.map(this::converterParaDTO);
    }

    public AutorDTO buscarPorId(Long id){
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado com ID: " + id));
        return converterParaDTO(autor);
    }

    public AutorDTO criar(AutorDTO autorDTO){
        validarEmailUnico(autorDTO.getEmail(), null);

        Autor autor = converterParaEntidade(autorDTO);
        Autor autorSalvo = autorRepository.save(autor);
        return converterParaDTO(autorSalvo);
    }

    public AutorDTO atualizar(Long id, AutorDTO autorDTO) {
        Autor autorExistente = autorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado com ID: " + id));

        validarEmailUnico(autorDTO.getEmail(), id);

        autorExistente.setNome(autorDTO.getNome());
        autorExistente.setEmail(autorDTO.getEmail());
        autorExistente.setDataNascimento(autorDTO.getDataNascimento());

        Autor autorAtualizado = autorRepository.save(autorExistente);
        return converterParaDTO(autorAtualizado);
    }

    public void deletar(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado com ID: " + id));

        if (!autor.getLivros().isEmpty()) {
            throw new IllegalStateException("Não é possível deletar autor que possui livros cadastrados");
        }

        autorRepository.delete(autor);
    }

    public List<LivroDTO> listarLivrosDoAutor(Long autorId){
        Autor autor = autorRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Autor não encontrado com ID: " + autorId));

        return autor.getLivros().stream().map(this::converterLivroParaDTO).toList();
    }

    private void validarEmailUnico(String email, Long id) {
        Optional<Autor> autorExistente = autorRepository.findByEmail(email);
        if (autorExistente.isPresent() && !autorExistente.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe um autor com este email: " + email);
        }
    }

    private AutorDTO converterParaDTO(Autor autor) {
        AutorDTO autorDTO = new AutorDTO();
        autorDTO.setId(autor.getId());
        autorDTO.setNome(autor.getNome());
        autorDTO.setEmail(autor.getEmail());
        autorDTO.setDataNascimento(autor.getDataNascimento());
        autorDTO.setTotalLivros(autor.getLivros().size());
        return autorDTO;
    }

    private Autor converterParaEntidade(AutorDTO dto) {
        Autor autor = new Autor();
        autor.setNome(dto.getNome());
        autor.setEmail(dto.getEmail());
        autor.setDataNascimento(dto.getDataNascimento());
        return autor;
    }

    private LivroDTO converterLivroParaDTO(Livro livro){
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
