package com.biblioteca.biblioteca_api.repository;

import com.biblioteca.biblioteca_api.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNome(String nome);

    @Query("SELECT c FROM Categoria c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Categoria> buscarPorNomeIgnorandoCase(@Param("nome") String nome, Pageable pageable);

    @Query("SELECT COUNT(l) FROM Livro l WHERE l.categoria.id = :categoriaId")
    Long contarLivrosPorCategoria(@Param("categoriaId") Long categoriaId);
}
