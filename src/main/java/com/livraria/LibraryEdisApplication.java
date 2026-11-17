package com.livraria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryEdisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryEdisApplication.class, args);
    }
}


// Limpar a porta 8080:
// netstat -ano | findstr :8080
// taskkill /PID <PID> /F

// Postgres 17
// psql -U postgres -h localhost
// Inserir a senha
// Para conectar o banco: java -jar target/LibraryEdis-0.0.1-SNAPSHOT.jar

// Dar start na aplicação:
// mvn spring-boot:run

// SET client_encoding = 'LATIN1';
// \dt  *para ver as tabelas do banco*