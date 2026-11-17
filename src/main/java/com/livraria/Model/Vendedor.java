package com.livraria.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "vendedor")
@PrimaryKeyJoinColumn(name = "cpf") // <--- Informa que o CPF do vendedor é PK e FK para a tabela pessoa
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Vendedor extends Pessoa {

    @Column(unique = true, nullable = false)
    private String matricula;

    @Column(name = "data_entrada")
    private LocalDate dataEntrada;

    @Column(name = "tempo_servico")
    private Integer tempoServico;

    private Double salario;
}