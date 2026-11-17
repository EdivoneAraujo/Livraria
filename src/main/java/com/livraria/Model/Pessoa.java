package com.livraria.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "pessoa")
@Inheritance(strategy = InheritanceType.JOINED) // <--- Essencial para criar/usar a tabela "pessoa"
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Pessoa {

    @Id
    @Column(name = "cpf", nullable = false, length = 255)
    private String cpf;

    @Column(name = "nome", nullable = false)
    private String nome;

    private String email;
    private String telefone;
}