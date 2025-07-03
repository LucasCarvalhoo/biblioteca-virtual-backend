package com.biblioteca.biblioteca_api.repository;

import com.biblioteca.biblioteca_api.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByIsbn(String isbn);

    @Query("SELECT l FROM Livro l WHERE " +
            "(:categoriaId IS NULL OR l.categoria.id = :categoriaId) AND " +
            "(:anoPublicacao IS NULL OR l.anoPublicacao = :anoPublicacao) AND " +
            "(:autorId IS NULL OR l.autor.id = :autorId)")
    Page<Livro> buscarComFiltros(@Param("categoriaId") Long categoriaId,
                                 @Param("anoPublicacao") Integer anoPublicacao,
                                 @Param("autorId") Long autorId,
                                 Pageable pageable);

    boolean existsByIsbn(String isbn);

    List<Livro> findByTituloContainingIgnoreCase(String titulo);

    Page<Livro> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    Long countByAutorId(Long autorId);

    Long countByCategoriaId(Long categoriaId);
}
