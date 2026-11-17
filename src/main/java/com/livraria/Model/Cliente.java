package com.livraria.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "cpf") // <--- Informa que o CPF do cliente é PK e FK para a tabela pessoa
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cliente extends Pessoa {

    private String sexo;
    private String uf;
}