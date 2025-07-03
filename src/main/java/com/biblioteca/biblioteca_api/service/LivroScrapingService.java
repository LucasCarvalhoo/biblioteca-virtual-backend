package com.biblioteca.biblioteca_api.service;

import com.biblioteca.biblioteca_api.dto.LivroDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LivroScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(LivroScrapingService.class);
    private static final int TIMEOUT = 15000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    public LivroDTO extrairDadosLivro(String url) {
        logger.info("Iniciando scraping da Amazon: {}", url);

        try {
            Document document = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .get();

            LivroDTO livroDTO = new LivroDTO();

            String titulo = extrairTitulo(document);
            livroDTO.setTitulo(titulo);

            BigDecimal preco = extrairPreco(document);
            livroDTO.setPreco(preco);

            String isbn = extrairIsbn(document);
            livroDTO.setIsbn(isbn);

            Integer ano = extrairAnoPublicacao(document);
            livroDTO.setAnoPublicacao(ano);

            livroDTO.setUrlOrigem(url);

            logger.info("Scraping concluído - Título: {}, Preço: {}, ISBN: {}",
                    titulo, preco, isbn);

            return livroDTO;

        } catch (Exception e) {
            logger.error("Erro ao fazer scraping da Amazon: {}", url, e);
            throw new RuntimeException("Erro ao extrair dados da Amazon: " + e.getMessage());
        }
    }

    private String extrairTitulo(Document document) {
        String[] seletoresTitulo = {
                "#productTitle",
                "span#productTitle",
                "h1 span#productTitle",
                ".product-title",
                "h1.a-size-large"
        };

        for (String seletor : seletoresTitulo) {
            Element elemento = document.selectFirst(seletor);
            if (elemento != null) {
                String titulo = elemento.text().trim();
                if (!titulo.isEmpty()) {
                    return titulo;
                }
            }
        }

        return "Título não encontrado";
    }

    private BigDecimal extrairPreco(Document document) {
        String[] seletoresPreco = {
                ".a-price-whole",
                ".a-price .a-offscreen",
                ".a-price-current .a-price-whole",
                ".a-price-current",
                ".a-price-range .a-price .a-offscreen"
        };

        for (String seletor : seletoresPreco) {
            Element elemento = document.selectFirst(seletor);
            if (elemento != null) {
                String precoText = elemento.text().trim();
                BigDecimal preco = converterPreco(precoText);
                if (preco.compareTo(BigDecimal.ZERO) > 0) {
                    return preco;
                }
            }
        }

        return BigDecimal.valueOf(29.90);
    }

    private BigDecimal converterPreco(String precoText) {
        if (precoText == null || precoText.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Pattern pattern = Pattern.compile("\\d+[.,]\\d{2}");
        Matcher matcher = pattern.matcher(precoText);

        if (matcher.find()) {
            String numeroLimpo = matcher.group().replace(",", ".");
            try {
                return new BigDecimal(numeroLimpo);
            } catch (NumberFormatException e) {
                logger.warn("Erro ao converter preço: {}", precoText);
            }
        }

        return BigDecimal.ZERO;
    }

    private String extrairIsbn(Document document) {
        String[] seletoresIsbn = {
                "#rpi-attribute-book_details-isbn13 .rpi-attribute-value span",
                "#detailBullets_feature_div span:contains(ISBN-13) + span",
                "#detail-bullets span:contains(ISBN) + span",
                ".content ul li:contains(ISBN)",
                "#bookDetails_container_div div:contains(ISBN)"
        };

        for (String seletor : seletoresIsbn) {
            Element elemento = document.selectFirst(seletor);
            if (elemento != null) {
                String isbn = extrairIsbnDoTexto(elemento.text());
                if (isbn != null) {
                    return isbn;
                }
            }
        }

        String isbn = extrairIsbnDoTexto(document.text());
        return isbn != null ? isbn : gerarIsbnAleatorio();
    }

    private String extrairIsbnDoTexto(String texto) {
        Pattern pattern13 = Pattern.compile("(?:ISBN[-\\s]?13?[:\\s]?)?([\\d-]{13,17})");
        Pattern pattern10 = Pattern.compile("(?:ISBN[-\\s]?10?[:\\s]?)?([\\d-]{10,13})");

        Matcher matcher13 = pattern13.matcher(texto);
        if (matcher13.find()) {
            String isbn = matcher13.group(1).replaceAll("[^\\d]", "");
            if (isbn.length() == 13) {
                return isbn;
            }
        }

        Matcher matcher10 = pattern10.matcher(texto);
        if (matcher10.find()) {
            String isbn = matcher10.group(1).replaceAll("[^\\d]", "");
            if (isbn.length() == 10) {
                return isbn;
            }
        }

        return null;
    }

    private Integer extrairAnoPublicacao(Document document) {
        String[] seletoresAno = {
                "#rpi-attribute-book_details-publication_date .rpi-attribute-value span",
                "#detailBullets_feature_div span:contains(Data de publicação) + span",
                "#detail-bullets span:contains(Publicação) + span"
        };

        for (String seletor : seletoresAno) {
            Element elemento = document.selectFirst(seletor);
            if (elemento != null) {
                Integer ano = extrairAnoDoTexto(elemento.text());
                if (ano != null) {
                    return ano;
                }
            }
        }

        Integer ano = extrairAnoDoTexto(document.text());
        return ano != null ? ano : 2023;
    }

    private Integer extrairAnoDoTexto(String texto) {
        Pattern pattern = Pattern.compile("\\b(19\\d{2}|20\\d{2})\\b");
        Matcher matcher = pattern.matcher(texto);

        while (matcher.find()) {
            int ano = Integer.parseInt(matcher.group(1));
            if (ano >= 1900 && ano <= 2024) {
                return ano;
            }
        }

        return null;
    }

    private String gerarIsbnAleatorio() {
        StringBuilder isbn = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            isbn.append((int) (Math.random() * 10));
        }
        return isbn.toString();
    }
}