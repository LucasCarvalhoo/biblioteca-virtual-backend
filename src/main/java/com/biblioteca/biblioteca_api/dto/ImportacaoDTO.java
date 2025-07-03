package com.biblioteca.biblioteca_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ImportacaoDTO {

    @NotBlank(message = "URL é obrigatória")
    @Pattern(regexp = "^https?://.*", message = "URL deve começar com http:// ou https://")
    private String url;

    @NotNull(message = "ID do autor é obrigatório")
    private Long autorId;

    @NotNull(message = "ID da categoria é obrigatório")
    private Long categoriaId;

    private LivroDTO livroImportado;
    private String status;
    private String mensagem;

    public ImportacaoDTO() {
    }

    public ImportacaoDTO(String url, Long autorId, Long categoriaId, LivroDTO livroImportado, String status, String mensagem) {
        this.url = url;
        this.autorId = autorId;
        this.categoriaId = categoriaId;
        this.livroImportado = livroImportado;
        this.status = status;
        this.mensagem = mensagem;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getAutorId() {
        return autorId;
    }

    public void setAutorId(Long autorId) {
        this.autorId = autorId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public LivroDTO getLivroImportado() {
        return livroImportado;
    }

    public void setLivroImportado(LivroDTO livroImportado) {
        this.livroImportado = livroImportado;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}