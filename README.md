# 🍽️ Gestão API - Tech Challenge Fase 1

Sistema de gestão para restaurantes desenvolvido como parte do Tech Challenge FIAP. Este é um backend completo para gerenciar usuários (clientes e donos de restaurantes) com autenticação JWT, validação robusta e documentação Swagger.

## 📋 Índice

- [Características](#características)
- [Pré-requisitos](#pré-requisitos)
- [Execução Rápida com Docker](#execução-rápida-com-docker)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Endpoints Disponíveis](#endpoints-disponíveis)
- [Autenticação](#autenticação)
- [Banco de Dados](#banco-de-dados)
- [Exemplos de Uso](#exemplos-de-uso)
- [Documentação Swagger](#documentação-swagger)

---

## ✨ Características

### Funcionalidades Principais
- ✅ **Gestão de Usuários**: Cadastro, atualização, exclusão e busca
- ✅ **Autenticação JWT**: Segurança com token Bearer
- ✅ **Tipos de Usuários**: Clientes e Donos de Restaurantes
- ✅ **Validações Robustas**: Jakarta Validation com mensagens em português
- ✅ **Troca de Senha**: Endpoint separado para segurança
- ✅ **Busca por Nome**: Filtrar usuários por nome
- ✅ **Paginação**: Listagem com controle de página e tamanho
- ✅ **API Versionada**: `/api/v1/`
- ✅ **Documentação Swagger**: Interativa e completa

### Stack Tecnológico
- **Java 17**
- **Spring Boot 3.0.3**
- **PostgreSQL**
- **Spring Security + JWT**
- **Flyway** (migrations)
- **Swagger/OpenAPI 3.0**
- **Docker & Docker Compose**
- **Maven**

---

## 🔧 Pré-requisitos

### Instalação Local
- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL 12+**
- **Git**

### Com Docker (Recomendado)
- **Docker 20.10+**
- **Docker Compose 1.29+**

---

## 🚀 Execução Rápida com Docker

### 1. Clonar o Repositório
```bash
git clone <seu-repositorio>
cd tech_challenge_fase1
```

### 2. Iniciar os Serviços
```bash
docker compose up -d
```

Isso irá:
- Criar e iniciar o container PostgreSQL
- Banco de dados: `mydatabase`
- Usuário: `myuser`
- Senha: `secret`
- Porta: `5432`

### 3. Compilar e Executar a Aplicação
```bash
./mvnw.cmd clean package
./mvnw.cmd spring-boot:run
```

Ou direto com Maven (em PowerShell):
```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

### 4. Acessar a Aplicação
- **API Base**: `http://localhost:8080/api/v1`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### 5. Parar os Serviços
```bash
docker compose down
```

---

## 📁 Estrutura do Projeto

```
tech_challenge_fase1/
├── src/
│   ├── main/
│   │   ├── java/br/com/fiap/Gestao/
│   │   │   ├── Application.java                 # Entry point
│   │   │   ├── config/
│   │   │   │   ├── AuthFilter.java             # Filtro JWT
│   │   │   │   ├── SecurityConfig.java         # Configuração Spring Security
│   │   │   │   └── SwaggerConfig.java          # Configuração OpenAPI
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java         # Autenticação
│   │   │   │   └── UsuarioController.java      # Gestão de usuários
│   │   │   ├── convertordto/
│   │   │   │   └── UsuarioMapper.java          # DTO → Entity
│   │   │   ├── dto/
│   │   │   │   ├── ApiErrorDTO.java            # Resposta de erro
│   │   │   │   ├── LoginRequestDTO.java        # Login
│   │   │   │   ├── TrocarSenhaRequestDTO.java  # Troca senha
│   │   │   │   ├── UsuarioRequestDTO.java      # Criar usuário
│   │   │   │   └── UsuarioUpdateDTO.java       # Atualizar usuário
│   │   │   ├── exception/
│   │   │   │   ├── CredenciaisInvalidasException.java
│   │   │   │   ├── SenhaInvalidaException.java
│   │   │   │   ├── UsuarioDuplicadoException.java
│   │   │   │   └── UsuarioNotFoundException.java
│   │   │   ├── handler/
│   │   │   │   └── GlobalExceptionHandler.java  # Tratamento central de erros
│   │   │   ├── jwt/
│   │   │   │   ├── Credentials.java
│   │   │   │   └── Token.java
│   │   │   ├── model/
│   │   │   │   ├── Endereco.java               # Endereço embarcado
│   │   │   │   ├── TipoUsuario.java            # Enum de tipos
│   │   │   │   └── Usuario.java                # Entidade principal
│   │   │   ├── repository/
│   │   │   │   └── UsuarioRepository.java      # Acesso a dados
│   │   │   └── service/
│   │   │       ├── AuthService.java            # Lógica de autenticação
│   │   │       ├── TokenService.java           # Geração/validação JWT
│   │   │       └── UsuarioService.java         # Lógica de usuários
│   │   └── resources/
│   │       ├── application.properties           # Configurações
│   │       └── db/migration/
│   │           └── V1__Create_Usuario_Table.sql # Schema
│   └── test/
│       └── java/br/com/fiap/Gestao/
│           └── ApplicationTests.java
├── compose.yaml                  # Docker Compose
├── pom.xml                        # Dependências Maven
├── AGENTS.md                      # Guia para agentes de IA
├── MEMORY.md                      # Progresso do projeto
└── README.md                      # Este arquivo
```

---

## 📡 Endpoints Disponíveis

### 🔐 Autenticação
| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/api/v1/login` | Autenticar usuário | ❌ Pública |

### 👥 Usuários
| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/api/v1/usuarios` | Listar usuários (paginado) | ❌ Pública |
| GET | `/api/v1/usuarios/{id}` | Buscar usuário por ID | ❌ Pública |
| GET | `/api/v1/usuarios/search/nome?nome=name` | Buscar por nome | ❌ Pública |
| POST | `/api/v1/usuarios` | Criar novo usuário | ❌ Pública |
| PUT | `/api/v1/usuarios/{id}` | Atualizar dados (sem senha) | ❌ Pública |
| PATCH | `/api/v1/usuarios/{id}/senha` | Trocar senha | ❌ Pública |
| DELETE | `/api/v1/usuarios/{id}` | Deletar usuário | ❌ Pública |

---

## 🔑 Autenticação

### Fluxo de Autenticação

#### 1. Login
```bash
POST /api/v1/login
Content-Type: application/json

{
  "email": "usuario@email.com",
  "senha": "SenhaForte123!"
}
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "usuario@email.com"
}
```

#### 2. Usar Token em Requisições Autenticadas
```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Estrutura do Token JWT
- **Subject**: ID do usuário
- **Email**: Email do usuário
- **Role**: Tipo de usuário (CLIENTE ou DONO_RESTAURANTE)
- **Expiração**: 2 horas (120 minutos)

---

## 🗄️ Banco de Dados

### Conexão
```
URL: jdbc:postgresql://localhost:5432/mydatabase
Usuário: myuser
Senha: secret
```

### Tabela: usuarios
```sql
CREATE TABLE usuarios (
  id BIGINT PRIMARY KEY,
  nome VARCHAR(255),
  email VARCHAR(255) UNIQUE NOT NULL,
  login_username VARCHAR(255) UNIQUE NOT NULL,
  senha VARCHAR(255) NOT NULL,
  tipo_usuario VARCHAR(50) CHECK (tipo_usuario IN ('CLIENTE', 'DONO_RESTAURANTE')),
  rua VARCHAR(120),
  numero VARCHAR(10),
  cidade VARCHAR(80),
  estado VARCHAR(2),
  cep VARCHAR(10),
  data_ultima_alteracao TIMESTAMP
);
```

### Migrations
As migrations são gerenciadas pelo **Flyway** e localizadas em:
```
src/main/resources/db/migration/
```

Versões implementadas:
- `V1__Create_Usuario_Table.sql` - Criação da tabela de usuários

---

## 📚 Exemplos de Uso

### 1️⃣ Criar um Usuário
```bash
POST /api/v1/usuarios
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "loginUsername": "joao_silva",
  "senha": "SenhaSegura123!",
  "tipoUsuario": "CLIENTE",
  "endereco": {
    "rua": "Rua das Flores",
    "numero": "123",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01310100"
  }
}
```

**Resposta (201 Created):**
```
Sem corpo (apenas status)
```

---

### 2️⃣ Fazer Login
```bash
POST /api/v1/login
Content-Type: application/json

{
  "email": "joao@email.com",
  "senha": "SenhaSegura123!"
}
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJqb2FvQGVtYWlsLmNvbSIsInJvbGUiOiJDTElFTlRFIiwiZXhwIjoxNzE0NDA5NjAwfQ.XYZ...",
  "email": "joao@email.com"
}
```

---

### 3️⃣ Listar Usuários (Paginado)
```bash
GET /api/v1/usuarios?page=1&size=10
```

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "loginUsername": "joao_silva",
    "tipoUsuario": "CLIENTE",
    "dataUltimaAlteracao": "2026-04-17T10:30:00",
    "endereco": {
      "rua": "Rua das Flores",
      "numero": "123",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01310100"
    }
  }
]
```

---

### 4️⃣ Atualizar Usuário (sem senha)
```bash
PUT /api/v1/usuarios/1
Content-Type: application/json

{
  "nome": "João Silva Atualizado",
  "email": "joao.novo@email.com",
  "loginUsername": "joao_silva_novo",
  "tipoUsuario": "DONO_RESTAURANTE",
  "endereco": {
    "rua": "Rua Nova",
    "numero": "456",
    "cidade": "Rio de Janeiro",
    "estado": "RJ",
    "cep": "20040020"
  }
}
```

**Resposta (200 OK):**
```json
{
  "id": 1,
  "nome": "João Silva Atualizado",
  "email": "joao.novo@email.com",
  "loginUsername": "joao_silva_novo",
  "tipoUsuario": "DONO_RESTAURANTE",
  "dataUltimaAlteracao": "2026-04-17T11:45:00",
  "endereco": {...}
}
```

---

### 5️⃣ Trocar Senha
```bash
PATCH /api/v1/usuarios/1/senha
Content-Type: application/json

{
  "senhaAtual": "SenhaSegura123!",
  "novaSenha": "NovaSenha456!",
  "confirmacaoNovaSenha": "NovaSenha456!"
}
```

**Resposta (204 No Content):**
```
Sem corpo
```

---

### 6️⃣ Buscar Usuários por Nome
```bash
GET /api/v1/usuarios/search/nome?nome=João
```

**Resposta (200 OK):**
```json
[
  {
    "nome": "João Silva",
    "email": "joao@email.com",
    "loginUsername": "joao_silva",
    "tipoUsuario": "CLIENTE",
    "endereco": {...}
  }
]
```

---

### 7️⃣ Deletar Usuário
```bash
DELETE /api/v1/usuarios/1
```

**Resposta (204 No Content):**
```
Sem corpo
```

---

### ❌ Erros Comuns

#### Email Duplicado (409 Conflict)
```json
{
  "timeError": "2026-04-17T10:30:00",
  "message": "Email já cadastrado no sistema",
  "error": "Conflict",
  "path": "/api/v1/usuarios",
  "fieldErrors": null
}
```

#### Usuário não Encontrado (404 Not Found)
```json
{
  "timeError": "2026-04-17T10:30:00",
  "message": "Usuario nao encontrado para o id 999",
  "error": "Not Found",
  "path": "/api/v1/usuarios/999",
  "fieldErrors": null
}
```

#### Validação Falhou (400 Bad Request)
```json
{
  "timeError": "2026-04-17T10:30:00",
  "message": "Erro de validacao",
  "error": "Bad Request",
  "path": "/api/v1/usuarios",
  "fieldErrors": {
    "email": "email invalido",
    "nome": "nome deve ter entre 3 e 120 caracteres"
  }
}
```

---

## 📖 Documentação Swagger

### Acessar Swagger UI
Após iniciar a aplicação, acesse:
```
http://localhost:8080/swagger-ui.html
```

### Características da Documentação
- ✅ Todos os endpoints documentados
- ✅ Exemplos de requisição e resposta
- ✅ Autenticação JWT integrada
- ✅ Códigos de status HTTP
- ✅ Descrições em português

### Testar via Swagger
1. Abra `http://localhost:8080/swagger-ui.html`
2. Faça login com `/api/v1/login`
3. Copie o token retornado
4. Clique em "Authorize" e cole o token
5. Todos os endpoints agora usarão o token automaticamente

---

## 🧪 Testando a Aplicação

### Com Postman
1. Importe a coleção fornecida: `Gestao_API_Postman.json`
2. Configure a variável `baseUrl` para `http://localhost:8080`
3. Execute os testes na ordem sugerida

### Com cURL
```bash
# Criar usuário
curl -X POST http://localhost:8080/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João",
    "email": "joao@email.com",
    "loginUsername": "joao",
    "senha": "SenhaSegura123!",
    "tipoUsuario": "CLIENTE",
    "endereco": {
      "rua": "Rua",
      "numero": "123",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01310100"
    }
  }'

# Login
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "SenhaSegura123!"
  }'
```

---

## 🛠️ Desenvolvimento

### Construir Projeto
```bash
./mvnw.cmd clean package
```

### Executar Testes
```bash
./mvnw.cmd test
```

### Verificar Erros
```bash
./mvnw.cmd clean compile
```

### Gerar JAR Executável
```bash
./mvnw.cmd clean package -DskipTests
java -jar target/Gestao-0.0.1-SNAPSHOT.jar
```

---

## 📋 Validações Implementadas

### Campos de Usuário
| Campo | Validação | Exemplo |
|-------|-----------|---------|
| Nome | 3-120 caracteres | "João Silva" |
| Email | Email válido + único | "joao@email.com" |
| Login | 3-50 caracteres + único | "joao_silva" |
| Senha | Min 8 caracteres | "SenhaForte123!" |
| CEP | Formato XX.XXX-XXX ou XXXXXXX | "01310100" |
| Estado | Exatamente 2 caracteres | "SP" |
| Tipo | CLIENTE ou DONO_RESTAURANTE | "CLIENTE" |

---

## 📞 Suporte e Problemas

### Porta já em uso (8080)
```bash
lsof -i :8080  # Mac/Linux
netstat -ano | findstr :8080  # Windows
```

### Banco não conecta
```bash
docker compose logs postgres
docker compose exec postgres psql -U myuser -d mydatabase
```

### Limpar tudo e reiniciar
```bash
docker compose down -v
docker compose up -d
```

---

## 📄 Licença

Este projeto foi desenvolvido para a disciplina de Tech Challenge - FIAP.

---

## 👨‍💻 Desenvolvedor

**Wesley** - Tech Challenge Fase 1 - 2026

---

## 🔗 Links Úteis

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [RFC 7807 - Problem Details](https://tools.ietf.org/html/rfc7807)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.0)
- [JWT.io](https://jwt.io/)
- [Docker Documentation](https://docs.docker.com/)

---

**Última atualização**: 2026-04-17
