package com.livraria.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "id_pessoa")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Administrador extends Pessoa {

    @Column(nullable = false, length = 50)
    private String perfil = "ADMIN";
}