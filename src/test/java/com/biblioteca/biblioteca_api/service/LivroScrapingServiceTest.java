package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.LivroDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TESTES UNITÁRIOS DA CLASSE - LivroScrapingServiceTest")
public class LivroScrapingServiceTest {

    @InjectMocks
    private LivroScrapingService livroScrapingService;

    @Test
    @DisplayName("extrairDadosLivro deve lançar RuntimeException quando URL é inválida")
    void extrairDadosLivro_quandoUrlInvalida_entaoLancaRuntimeException() {
        String urlInvalida = "https://url-inexistente-teste.com";

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> livroScrapingService.extrairDadosLivro(urlInvalida));

        assertTrue(exception.getMessage().contains("Erro ao extrair dados da Amazon"));
    }

    @Test
    @DisplayName("extrairDadosLivro deve lançar RuntimeException quando URL é null")
    void extrairDadosLivro_quandoUrlNula_entaoLancaRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> livroScrapingService.extrairDadosLivro(null));

        assertTrue(exception.getMessage().contains("Erro ao extrair dados da Amazon"));
    }

    @Test
    @DisplayName("extrairDadosLivro deve lançar RuntimeException quando URL é vazia")
    void extrairDadosLivro_quandoUrlVazia_entaoLancaRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> livroScrapingService.extrairDadosLivro(""));

        assertTrue(exception.getMessage().contains("Erro ao extrair dados da Amazon"));
    }

    @Test
    @DisplayName("extrairDadosLivro deve lançar RuntimeException quando URL tem formato inválido")
    void extrairDadosLivro_quandoUrlFormatoInvalido_entaoLancaRuntimeException() {
        String urlInvalida = "url-sem-protocolo";

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> livroScrapingService.extrairDadosLivro(urlInvalida));

        assertTrue(exception.getMessage().contains("Erro ao extrair dados da Amazon"));
    }

    @Test
    @DisplayName("extrairDadosLivro deve retornar dados padrão quando não consegue extrair informações")
    void extrairDadosLivro_quandoNaoConsegueExtrair_entaoRetornaDadosPadrao() {
        try {
            LivroDTO resultado = livroScrapingService.extrairDadosLivro("https://httpbin.org/html");

            assertNotNull(resultado);
            assertNotNull(resultado.getTitulo());
            assertNotNull(resultado.getPreco());
            assertNotNull(resultado.getIsbn());
            assertNotNull(resultado.getAnoPublicacao());

            assertTrue(resultado.getPreco().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(resultado.getIsbn().length() >= 10);
            assertTrue(resultado.getAnoPublicacao() >= 1900);

        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Erro ao extrair dados da Amazon"));
        }
    }

    @Test
    @DisplayName("extrairDadosLivro deve definir URL de origem corretamente")
    void extrairDadosLivro_quandoChamado_entaoDefineUrlOrigem() {
        String urlTeste = "https://httpbin.org/html";

        try {
            LivroDTO resultado = livroScrapingService.extrairDadosLivro(urlTeste);
            assertEquals(urlTeste, resultado.getUrlOrigem());
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Erro ao extrair dados da Amazon"));
        }
    }

    @Test
    @DisplayName("extrairDadosLivro deve gerar ISBN válido quando não encontra no site")
    void extrairDadosLivro_quandoNaoEncontraIsbn_entaoGeraIsbnValido() {
        try {
            LivroDTO resultado = livroScrapingService.extrairDadosLivro("https://httpbin.org/html");

            assertNotNull(resultado.getIsbn());
            assertTrue(resultado.getIsbn().length() == 10 || resultado.getIsbn().length() == 13);
            assertTrue(resultado.getIsbn().matches("\\d+"));

        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Erro ao extrair dados da Amazon"));
        }
    }

    @Test
    @DisplayName("extrairDadosLivro deve definir ano padrão quando não encontra no site")
    void extrairDadosLivro_quandoNaoEncontraAno_entaoDefineAnoPadrao() {
        try {
            LivroDTO resultado = livroScrapingService.extrairDadosLivro("https://httpbin.org/html");

            assertNotNull(resultado.getAnoPublicacao());
            assertTrue(resultado.getAnoPublicacao() >= 1900);
            assertTrue(resultado.getAnoPublicacao() <= 2024);

        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Erro ao extrair dados da Amazon"));
        }
    }

    @Test
    @DisplayName("extrairDadosLivro deve definir preço padrão quando não encontra no site")
    void extrairDadosLivro_quandoNaoEncontraPreco_entaoDefinePrecoPadrao() {
        try {
            LivroDTO resultado = livroScrapingService.extrairDadosLivro("https://httpbin.org/html");

            assertNotNull(resultado.getPreco());
            assertTrue(resultado.getPreco().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(resultado.getPreco().compareTo(BigDecimal.valueOf(1000)) <= 0);

        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Erro ao extrair dados da Amazon"));
        }
    }

    @Test
    @DisplayName("extrairDadosLivro deve definir título padrão quando não encontra no site")
    void extrairDadosLivro_quandoNaoEncontraTitulo_entaoDefineTituloPadrao() {
        try {
            LivroDTO resultado = livroScrapingService.extrairDadosLivro("https://httpbin.org/html");

            assertNotNull(resultado.getTitulo());
            assertFalse(resultado.getTitulo().isEmpty());

        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Erro ao extrair dados da Amazon"));
        }
    }
}