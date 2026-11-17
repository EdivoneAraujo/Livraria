package com.livraria.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "usuario")
@PrimaryKeyJoinColumn(name = "cpf") // <--- Garante a ligação de herança com a tabela pessoa via CPF
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Usuario extends Pessoa {

    @Column(nullable = false)
    private String senha;

    private String perfil; // Ex: "ADMIN"
}