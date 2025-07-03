package com.biblioteca.biblioteca_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;

    private Integer totalLivros;

    public CategoriaDTO() {
    }

    public CategoriaDTO(Long id, String nome, String descricao, Integer totalLivros) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.totalLivros = totalLivros;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getTotalLivros() {
        return totalLivros;
    }

    public void setTotalLivros(Integer totalLivros) {
        this.totalLivros = totalLivros;
    }
}