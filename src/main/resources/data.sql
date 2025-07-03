INSERT INTO autores (nome, email, data_nascimento) VALUES
('Yuval Noah Harari', 'yuval.harari@email.com', '1976-02-24'),
('Charles Duhigg', 'charles.duhigg@email.com', '1974-01-01'),
('James Clear', 'james.clear@email.com', '1986-01-01'),
('George S. Clason', 'george.clason@email.com', '1874-11-07'),
('Robert T. Kiyosaki', 'robert.kiyosaki@email.com', '1947-04-08'),
('Napoleon Hill', 'napoleon.hill@email.com', '1883-10-26'),
('Dale Carnegie', 'dale.carnegie@email.com', '1888-11-24'),
('Paulo Coelho', 'paulo.coelho@email.com', '1947-08-24');

INSERT INTO categorias (nome, descricao) VALUES
('História', 'Livros sobre história da humanidade e civilizações'),
('Desenvolvimento Pessoal', 'Livros de autoajuda e crescimento pessoal'),
('Produtividade', 'Livros sobre hábitos e produtividade'),
('Finanças', 'Livros sobre educação financeira e investimentos'),
('Negócios', 'Livros sobre empreendedorismo e business'),
('Psicologia', 'Livros sobre comportamento humano e psicologia'),
('Ficção', 'Livros de ficção e literatura'),
('Filosofia', 'Livros de filosofia e pensamento');

INSERT INTO livros (titulo, isbn, ano_publicacao, preco, autor_id, categoria_id, url_origem, data_cadastro, data_atualizacao) VALUES
('Sapiens: Uma Breve História da Humanidade', '9788525432180', 2014, 39.90, 1, 1, 'https://www.amazon.com.br/Sapiens-Uma-Breve-Hist%C3%B3ria-Humanidade/dp/8525432180', NOW(), NOW()),
('O Poder do Hábito', '9788539004119', 2012, 34.90, 2, 2, 'https://www.amazon.com.br/poder-do-h%C3%A1bito-Charles-Duhigg/dp/8539004119', NOW(), NOW()),
('Hábitos Atômicos', '9788550807567', 2019, 42.90, 3, 3, 'https://www.amazon.com.br/H%C3%A1bitos-At%C3%B4micos-M%C3%A9todo-Comprovado-Livrar/dp/8550807567', NOW(), NOW()),
('O Homem Mais Rico da Babilônia', '9788595081530', 1926, 24.90, 4, 4, 'https://www.amazon.com.br/Homem-Mais-Rico-Babil%C3%B4nia/dp/8595081530', NOW(), NOW()),
('Pai Rico, Pai Pobre', '9788550801488', 1997, 29.90, 5, 4, 'https://www.amazon.com.br/Pai-Rico-Pobre-atualizada-ampliada-ebook/dp/8550801488', NOW(), NOW());
