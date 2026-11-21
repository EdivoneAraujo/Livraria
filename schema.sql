-- 1. Superclasse PESSOA
CREATE TABLE IF NOT EXISTS pessoa (
    id_pessoa BIGSERIAL PRIMARY KEY,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

-- 2. Atributo multivalorado TELEFONES de PESSOA (1FN)
CREATE TABLE IF NOT EXISTS telefone_pessoa (
    id_pessoa BIGINT NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_pessoa, telefone),
    CONSTRAINT fk_telefone_pessoa
        FOREIGN KEY (id_pessoa) REFERENCES pessoa(id_pessoa)
        ON DELETE CASCADE
);

-- 3. Especialização ADMINISTRADOR (Herança JOINED de PESSOA)
CREATE TABLE IF NOT EXISTS administrador (
    id_pessoa BIGINT PRIMARY KEY,
    perfil VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
    CONSTRAINT fk_administrador_pessoa
        FOREIGN KEY (id_pessoa) REFERENCES pessoa(id_pessoa)
        ON DELETE CASCADE
);

-- 4. Especialização VENDEDOR (Herança JOINED de PESSOA + auto-relacionamento GERENCIA)
CREATE TABLE IF NOT EXISTS vendedor (
    id_pessoa BIGINT PRIMARY KEY,
    matricula VARCHAR(30) NOT NULL UNIQUE,
    salario NUMERIC(10, 2) NOT NULL CHECK (salario > 0),
    data_entrada DATE NOT NULL,
    id_supervisor BIGINT NULL,
    id_administrador BIGINT NULL,
    CONSTRAINT fk_vendedor_pessoa
        FOREIGN KEY (id_pessoa) REFERENCES pessoa(id_pessoa)
        ON DELETE CASCADE,
    CONSTRAINT fk_vendedor_supervisor
        FOREIGN KEY (id_supervisor) REFERENCES vendedor(id_pessoa)
        ON DELETE SET NULL,
    CONSTRAINT fk_vendedor_administrador
        FOREIGN KEY (id_administrador) REFERENCES administrador(id_pessoa)
        ON DELETE SET NULL
);

-- 5. Especialização CLIENTE (Herança JOINED de PESSOA + endereço completo + auto-relacionamento INDICA)
CREATE TABLE IF NOT EXISTS cliente (
    id_pessoa BIGINT PRIMARY KEY,
    sexo VARCHAR(20),
    rua VARCHAR(150) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    uf CHAR(2) NOT NULL,
    cep VARCHAR(9) NOT NULL,
    id_cliente_indicador BIGINT NULL,
    CONSTRAINT fk_cliente_pessoa
        FOREIGN KEY (id_pessoa) REFERENCES pessoa(id_pessoa)
        ON DELETE CASCADE,
    CONSTRAINT fk_cliente_indicador
        FOREIGN KEY (id_cliente_indicador) REFERENCES cliente(id_pessoa)
        ON DELETE SET NULL
);

-- 6. Entidade AUTOR
CREATE TABLE IF NOT EXISTS autor (
    id_autor BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL
);

-- 7. Entidade LIVRO (relacionamento CADASTRA com ADMINISTRADOR)
CREATE TABLE IF NOT EXISTS livro (
    id_livro BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    tematica VARCHAR(100) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL CHECK (preco >= 0),
    quantidade_estoque INT NOT NULL DEFAULT 0 CHECK (quantidade_estoque >= 0),
    estoque_minimo INT NOT NULL DEFAULT 5 CHECK (estoque_minimo >= 0),
    imagem VARCHAR(255),
    id_administrador BIGINT NULL,
    CONSTRAINT fk_livro_administrador
        FOREIGN KEY (id_administrador) REFERENCES administrador(id_pessoa)
        ON DELETE SET NULL
);

-- 8. Tabela associativa LIVRO_AUTOR (relacionamento ESCRITO_POR N:M)
CREATE TABLE IF NOT EXISTS livro_autor (
    id_livro BIGINT NOT NULL,
    id_autor BIGINT NOT NULL,
    PRIMARY KEY (id_livro, id_autor),
    CONSTRAINT fk_livro_autor_livro
        FOREIGN KEY (id_livro) REFERENCES livro(id_livro)
        ON DELETE CASCADE,
    CONSTRAINT fk_livro_autor_autor
        FOREIGN KEY (id_autor) REFERENCES autor(id_autor)
        ON DELETE CASCADE
);

-- 9. Entidade PEDIDO (relacionamentos REALIZA com CLIENTE e EFETUA com VENDEDOR)
CREATE TABLE IF NOT EXISTS pedido (
    id_pedido BIGSERIAL PRIMARY KEY,
    data_venda TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    forma_pagamento VARCHAR(20) NOT NULL CHECK (forma_pagamento IN ('CARTAO', 'PIX')),
    id_cliente BIGINT NOT NULL,
    id_vendedor BIGINT NULL,
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_pessoa)
        ON DELETE RESTRICT,
    CONSTRAINT fk_pedido_vendedor
        FOREIGN KEY (id_vendedor) REFERENCES vendedor(id_pessoa)
        ON DELETE SET NULL
);

-- 10. Entidade Fraca ITEM_PEDIDO (chave composta: id_pedido + numero_item)
CREATE TABLE IF NOT EXISTS item_pedido (
    id_pedido BIGINT NOT NULL,
    numero_item INT NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco_unitario NUMERIC(10, 2) NOT NULL CHECK (preco_unitario >= 0),
    id_livro BIGINT NOT NULL,
    PRIMARY KEY (id_pedido, numero_item),
    CONSTRAINT fk_item_pedido_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
        ON DELETE CASCADE,
    CONSTRAINT fk_item_pedido_livro
        FOREIGN KEY (id_livro) REFERENCES livro(id_livro)
        ON DELETE RESTRICT
);

-- 11. Entidade FATURA (relacionamento GERA 1:1 com PEDIDO)
CREATE TABLE IF NOT EXISTS fatura (
    id_fatura BIGSERIAL PRIMARY KEY,
    numero_fatura VARCHAR(50) NOT NULL UNIQUE,
    data_emissao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_pedido BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_fatura_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
        ON DELETE CASCADE
);
