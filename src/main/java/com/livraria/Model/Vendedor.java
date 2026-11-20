package com.livraria.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "vendedor")
@PrimaryKeyJoinColumn(name = "id_pessoa")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"supervisor", "administrador"})
@ToString(callSuper = true, exclude = {"supervisor", "administrador"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Vendedor extends Pessoa {

    @Column(unique = true, nullable = false, length = 30)
    private String matricula;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

    @Column(nullable = false)
    private Double salario;

    // Auto-relacionamento GERENCIA (1 supervisor -> N vendedores)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_supervisor")
    @JsonIgnore
    private Vendedor supervisor;

    // Relacionamento REGISTRA (1 administrador -> N vendedores)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_administrador")
    @JsonIgnore
    private Administrador administrador;

    // Atributo derivado (calculado dinamicamente a partir de data_entrada)
    @Transient
    public Integer getTempoServico() {
        if (this.dataEntrada == null) {
            return 0;
        }
        return Period.between(this.dataEntrada, LocalDate.now()).getYears();
    }
}