<p align="center">
  <img src="src/main/resources/static/img/capa.png" alt="Capa da Livraria Tríade de Papel" width="100%">
</p>

<h1 align="center">📚 Livraria Tríade de Papel</h1>

<p align="center">
  <strong>Sistema Web Full-Stack de Gestão de Acervo Literário, Vendas e Faturamento</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white" alt="HTML5">
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white" alt="CSS3">
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" alt="JavaScript">
</p>

---

## 📖 Visão Geral

A **Livraria Tríade de Papel** é uma plataforma web full-stack desenvolvida para automatizar o controle de acervo bibliográfico, atendimento comercial (balcão e vitrine online), baixa transacional de estoque e emissão de faturas com pagamento via Cartão ou PIX.

O projeto foi concebido no contexto acadêmico do **Instituto Federal de Pernambuco (IFPE) – Campus Belo Jardim**, aplicando conceitos avançados de **Programação Orientada a Objetos (POO)**, persistência relacional normalizada com **Hibernate/JPA** (`InheritanceType.JOINED`) e arquitetura **RESTful**.

---

## 🎯 Requisitos do Sistema (SRS)

### Requisitos Funcionais

| Código | Requisito | Descrição |
| :--- | :--- | :--- |
| **RF01** | **Vitrine Digital e Busca** | Exibir catálogo de livros disponíveis em estoque com pesquisa em tempo real por título ou autor. |
| **RF02** | **Filtros de Acervo** | Permitir filtragem rápida do catálogo por **Temática**, **Lançamentos** e **Promoções**. |
| **RF03** | **Gestão de Clientes** | Permitir cadastro de novos clientes e autenticação via CPF para realização de compras e consulta de histórico. |
| **RF04** | **Gestão de Funcionários** | Permitir ao administrador cadastrar vendedores vinculados à herança `Pessoa` $\rightarrow$ `Vendedor` (CPF e matrícula). |
| **RF05** | **Cadastro e Manutenção de Acervo** | Permitir o registro e atualização de livros e autores, incluindo preço, temática, estoque atual e estoque mínimo. |
| **RF06** | **Carrinho de Compras** | Gerenciar itens adicionados ao pedido com ajuste dinâmico de quantidade e cálculo automático do valor total. |
| **RF07** | **Venda em Lote e Atendimento de Balcão** | Processar pedidos com múltiplos itens, permitindo que vendedores registrem vendas presenciais associadas ao CPF do cliente. |
| **RF08** | **Emissão de Fatura e Pagamento** | Gerar fatura única vinculada à venda com suporte a **Cartão** e **PIX** (exibição de QR Code e chave Copia e Cola). |
| **RF09** | **Controle e Alerta de Estoque** | Validar disponibilidade antes da venda, realizar baixa automática e emitir alertas quando o estoque atingir o limite mínimo. |
| **RF10** | **Painel de Relatórios e Métricas** | Consolidar faturamento total, volume de vendas, produto mais vendido, melhor cliente e pesquisa de faturas. |

### Requisitos Não Funcionais

| Código | Requisito | Descrição |
| :--- | :--- | :--- |
| **RNF01** | **Integridade Transacional** | Toda operação de venda em lote e baixa de estoque deve ser atômica (`@Transactional`), garantindo rollback em caso de falha. |
| **RNF02** | **Normalização Relacional** | O esquema do banco de dados deve seguir a 3ª Forma Normal (3FN), separando `Autor` de `Livro` e aplicando herança `JOINED` em `Pessoa`. |
| **RNF03** | **Validação de Esquema** | A aplicação deve validar o DDL físico do PostgreSQL na inicialização (`spring.jpa.hibernate.ddl-auto=validate`). |
| **RNF04** | **Segurança de Credenciais** | Credenciais de acesso ao banco de dados devem ser injetadas via variáveis de ambiente (`DB_USER`, `DB_PASSWORD`), sem exposição no repositório. |
| **RNF05** | **Responsividade de Interface** | O front-end deve adaptar-se a desktops e dispositivos móveis, incluindo gaveta lateral (*drawer*) para o carrinho. |

---

## 🏗️ Arquitetura e Modelagem

### Arquitetura em Camadas (Spring Boot)

```text
┌─────────────────────────────────────────────────────────────┐
│                    Front-end (SPA Estática)                 │
│              HTML5  •  CSS3  •  JavaScript (Fetch)          │
└──────────────────────────────┬──────────────────────────────┘
                               │ REST / JSON (HTTP :8080)
┌──────────────────────────────▼──────────────────────────────┐
│                     Camada Controller                       │
│  LivroController • AutorController • ClienteController      │
│  VendedorController • UsuarioController • VendaController   │
└──────────────────────────────┬──────────────────────────────┘
                               │ DTOs & Entidades
┌──────────────────────────────▼──────────────────────────────┐
│                  Camada Service (Negócio)                   │
│       LivroService  •  ClienteService  •  VendaService      │
└──────────────────────────────┬──────────────────────────────┘
                               │ Spring Data JPA / JPQL
┌──────────────────────────────▼──────────────────────────────┐
│                    Camada Repository                        │
│     Livro • Autor • Cliente • Vendedor • Usuario            │
│                    Venda • Fatura                           │
└──────────────────────────────┬──────────────────────────────┘
                               │ JDBC / Hibernate ORM
┌──────────────────────────────▼──────────────────────────────┐
│                  Banco de Dados PostgreSQL                  │
│             Schema Normalizado (Herança JOINED)             │
└─────────────────────────────────────────────────────────────┘
```
---
## Modelo Relacional (PostgreSQL)

- [x] **pessoa** (**cpf**, nome, email, telefone)
- [x] **cliente** (**_cpf_**, sexo, uf)
- [x] **usuario** (**_cpf_**, senha, perfil)
- [x] **vendedor** (**_cpf_**, matricula, data_entrada, tempo_servico, salario)
- [x] **autor** (**id**, nome)
- [x] **livro** (**id**, titulo, tematica, preco, quantidade_estoque, estoque_minimo, imagem, _autor_id_)
- [x] **venda** (**id**, data_venda, total, forma_pagamento, alerta_estoque, _cliente_cpf_, _vendedor_cpf_)
- [x] **item_venda** (**id**, _venda_id_, _livro_id_, quantidade, preco_unitario)
- [x] **fatura** (**id**, _venda_id_, numero_fatura, data_emissao)

*(Legenda: **Negrito** = Chave Primária | *Itálico* = Chave Estrangeira)*

---
## 📁 Estrutura de Diretórios

```text
LibraryEdis/
├── src/
│   └── main/
│       ├── java/com/livraria/
│       │   ├── Controller/         # Controladores REST (/api/*)
│       │   ├── DTO/                # Objetos de Transferência de Dados
│       │   ├── Model/              # Entidades JPA e Enums
│       │   ├── Repository/         # Repositórios Spring Data JPA
│       │   ├── Service/            # Regras de negócio e transações
│       │   └── LibraryEdisApplication.java
│       └── resources/
│           ├── static/
│           │   ├── img/            # Capa, logotipos e imagens do acervo
│           │   ├── index.html      # Interface principal (Loja, Cliente, Vendedor e Admin)
│           │   ├── style.css       # Folha de estilos responsiva
│           │   └── script.js       # Lógica de consumo da API e gerenciamento de estado
│           └── application.properties # Configurações de conexão e Hibernate
└── pom.xml                         # Dependências Maven
```
---
## ⚙️ Instalação e Execução

### 1. Pré-requisitos
* **Java Development Kit (JDK):** Versão 21 ou superior
* **PostgreSQL:** Ativo na porta padrão `5432`
* **Apache Maven:** Versão 3.9 ou superior

### 2. Configuração do Banco de Dados
Crie o banco de dados no PostgreSQL executando o comando abaixo:
```sql
CREATE DATABASE loja;
```

### 3. Variáveis de Ambiente
Defina as credenciais de acesso ao PostgreSQL nas variáveis de ambiente do sistema operacional ou nas configurações de execução da sua IDE (como IntelliJ IDEA ou VS Code):
```env
DB_USER=postgres
DB_PASSWORD=sua_senha
```

### 4. Executar o Projeto

1. **Clonar o repositório:**
   ```bash
   git clone [https://github.com/EdivoneAraujo/Livraria.git](https://github.com/EdivoneAraujo/Livraria.git)
   ```

2. **Acessar o diretório do projeto:**
   ```bash
   cd Livraria
   ```

3. **Compilar e iniciar a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

> **Acesso à aplicação:** Após a inicialização no console, abra o navegador e acesse [`http://localhost:8080/index.html`](http://localhost:8080/index.html).

---
## 👥 Equipe de Desenvolvimento

<p align="center">
  <a href="https://www.linkedin.com/in/edilka-vanessa-menezes-taurino"><img src="https://img.shields.io/badge/LinkedIn-Edilka%20Taurino-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn Edilka Taurino"></a>
  &nbsp;&nbsp;&nbsp;
  <a href="https://github.com/nessataurino-design"><img src="https://img.shields.io/badge/GitHub-Edilka%20Taurino-black?style=for-the-badge&logo=github" alt="GitHub Edilka Taurino"></a>
</p>

<p align="center">
  <a href="https://www.linkedin.com/in/edivone-araujo"><img src="https://img.shields.io/badge/LinkedIn-Edivone%20Araújo-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn Edivone Araújo"></a>
  &nbsp;&nbsp;&nbsp;
  <a href="https://github.com/EdivoneAraujo"><img src="https://img.shields.io/badge/GitHub-Edivone%20Araújo-black?style=for-the-badge&logo=github" alt="GitHub Edivone Araújo"></a>
</p>

<p align="center">
  <a href="https://www.linkedin.com/in/vinicius-cavalcanti-santos"><img src="https://img.shields.io/badge/LinkedIn-Vinícius%20Santos-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn Vinicius Cavalcanti Santos"></a>
  &nbsp;&nbsp;&nbsp;
  <a href="https://github.com/vinicius-cavalcanti-santos"><img src="https://img.shields.io/badge/GitHub-Vinícius%20Santos-black?style=for-the-badge&logo=github" alt="GitHub Vinicius Cavalcanti Santos"></a>
</p>
