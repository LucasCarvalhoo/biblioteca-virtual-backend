# Biblioteca API - Case Técnico

## 📋 Descrição

API REST para gerenciamento de biblioteca digital com funcionalidade de web scraping para importação de livros de sites externos.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **Spring Web**
- **Spring Validation**
- **H2 Database** (em memória)
- **JSoup** (web scraping)
- **Swagger/OpenAPI**
- **JUnit 5** (testes)
- **Maven**

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/biblioteca/biblioteca_api/
│   │   ├── controller/          # Controllers REST
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── model/               # Entidades JPA
│   │   ├── repository/          # Repositórios
│   │   ├── service/             # Serviços de negócio
│   │   ├── exception/           # Tratamento de exceções
│   │   └── config/              # Configurações
│   └── resources/
│       ├── application.properties
│       └── data.sql             # Dados iniciais
└── test/                        # Testes unitários
```

## 🛠️ Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+

### Passos

1. **Clone o repositório**
```bash
git clone <url-do-repositorio>
cd biblioteca-api
```

2. **Execute o projeto**
```bash
# Usando Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Ou usando Maven instalado
mvn spring-boot:run
```

3. **Acesse a aplicação**
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:biblioteca`
  - Username: `sa`
  - Password: `password`

## 📚 Endpoints da API

### **Autores**
- `GET /api/autores` - Listar todos os autores (paginado)
- `GET /api/autores/{id}` - Buscar autor por ID
- `POST /api/autores` - Criar novo autor
- `PUT /api/autores/{id}` - Atualizar autor
- `DELETE /api/autores/{id}` - Deletar autor
- `GET /api/autores/{id}/livros` - Listar livros do autor

### **Livros**
- `GET /api/livros` - Listar todos os livros (com filtros)
- `GET /api/livros/{id}` - Buscar livro por ID
- `POST /api/livros` - Criar novo livro
- `PUT /api/livros/{id}` - Atualizar livro
- `DELETE /api/livros/{id}` - Deletar livro
- `GET /api/livros/buscar?titulo={titulo}` - Buscar por título
- `POST /api/livros/importar` - Importar livro via web scraping

### **Categorias**
- `GET /api/categorias` - Listar todas as categorias
- `POST /api/categorias` - Criar nova categoria
- `GET /api/categorias/{id}/livros` - Listar livros da categoria

## 🔍 Exemplos de Uso

### **Criar um Autor**
```bash
curl -X POST http://localhost:8080/api/autores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Paulo Coelho",
    "email": "paulo@exemplo.com",
    "dataNascimento": "1947-08-24"
  }'
```

### **Criar uma Categoria**
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ficção",
    "descricao": "Livros de ficção literária"
  }'
```

### **Criar um Livro**
```bash
curl -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "O Alquimista",
    "isbn": "9788576653265",
    "anoPublicacao": 1988,
    "preco": 29.90,
    "autorId": 1,
    "categoriaId": 1
  }'
```

### **Importar Livro via Web Scraping**
```bash
curl -X POST http://localhost:8080/api/livros/importar \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.amazon.com.br/Sapiens-Uma-Breve-História-Humanidade/dp/8525432180",
    "autorId": 1,
    "categoriaId": 1
  }'
```

### **Buscar Livros com Filtros**
```bash
# Por categoria
curl "http://localhost:8080/api/livros?categoriaId=1"

# Por ano
curl "http://localhost:8080/api/livros?anoPublicacao=2020"

# Por autor
curl "http://localhost:8080/api/livros?autorId=1"

# Múltiplos filtros
curl "http://localhost:8080/api/livros?categoriaId=1&anoPublicacao=2020&page=0&size=10"
```

## 🕷️ Web Scraping

### **Sites Suportados**
- Amazon Brasil (implementado)

### **Funcionalidades**
- Extração automática de título, preço, ISBN e ano de publicação
- Validação de ISBN para evitar duplicatas
- Tratamento de erros e timeouts
- Logs detalhados das operações

### **URLs de Teste**
```
# Livros populares para teste
https://www.amazon.com.br/Sapiens-Uma-Breve-História-Humanidade/dp/8525432180
https://www.amazon.com.br/poder-do-hábito-Charles-Duhigg/dp/8539004119
https://www.amazon.com.br/Hábitos-Atômicos-Método-Comprovado-Livrar/dp/8550807567
```

## 📊 Dados Iniciais

O sistema vem com dados pré-carregados:
- 8 autores conhecidos
- 8 categorias diferentes
- 5 livros populares

## 🧪 Testes

### **Executar Testes**
```bash
# Todos os testes
./mvnw test

# Apenas testes de uma classe
./mvnw test -Dtest=AutorServiceTest

# Com relatório de cobertura
./mvnw test jacoco:report
```

### **Testes Implementados**
- ✅ Testes unitários para Services
- ✅ Mocks para dependências
- ✅ Testes de validação
- ✅ Testes de exceções

## 🔧 Configurações

### **Banco de Dados**
```properties
# H2 em memória (padrão)
spring.datasource.url=jdbc:h2:mem:biblioteca
spring.h2.console.enabled=true
```

### **Web Scraping**
```properties
# Configurações do scraping
scraping.amazon.timeout=15000
scraping.amazon.max-retries=3
scraping.amazon.delay-between-requests=1000
```

## 📋 Validações Implementadas

### **Autor**
- Nome obrigatório
- Email válido e único
- Data de nascimento anterior à atual

### **Livro**
- Título obrigatório (máx. 200 caracteres)
- ISBN válido (10 ou 13 dígitos)
- Ano não futuro
- Preço positivo
- Relacionamentos obrigatórios (autor e categoria)

### **Categoria**
- Nome obrigatório e único
- Descrição opcional (máx. 500 caracteres)

## 🚨 Tratamento de Erros

A API retorna códigos HTTP apropriados:
- `200` - Sucesso
- `201` - Criado
- `400` - Dados inválidos
- `404` - Não encontrado
- `409` - Conflito (ex: ISBN duplicado)
- `500` - Erro interno

## 📄 Documentação da API

Acesse a documentação interativa via Swagger:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

## 🔄 Collection do Postman

Importe o arquivo `Biblioteca-API.postman_collection.json` no Postman para testar todos os endpoints com URLs reais da Amazon.

## 📈 Melhorias Futuras

- [ ] Implementar autenticação JWT
- [ ] Adicionar cache Redis
- [ ] Implementar rate limiting
- [ ] Adicionar mais sites de scraping
- [ ] Implementar notificações
- [ ] Adicionar métricas com Micrometer

## 🐛 Problemas Conhecidos

- O scraping pode falhar se a Amazon alterar a estrutura HTML
- Timeout pode ocorrer em conexões lentas
- Alguns ISBNs podem não ser encontrados automaticamente

## 🤝 Contribuição

1. Faça o fork do projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Faça o push para a branch
5. Abra um Pull Request

## 📝 Licença

Este projeto está licenciado sob a licença MIT.

## 👨‍💻 Desenvolvedor

**Seu Nome**
- Email: seu.email@exemplo.com
- LinkedIn: https://linkedin.com/in/seu-perfil
- GitHub: https://github.com/seu-usuario

---

**Tempo de desenvolvimento:** 4 horas
**Última atualização:** Julho 2025
