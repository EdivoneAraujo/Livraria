package com.livraria.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id_pessoa")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"clienteIndicador"})
@ToString(callSuper = true, exclude = {"clienteIndicador"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cliente extends Pessoa {

    private String sexo;

    @Column(nullable = false, length = 150)
    private String rua;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(nullable = false, length = 9)
    private String cep;

    // Auto-relacionamento INDICA (1 cliente indicador -> N clientes indicados)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_indicador")
    @JsonIgnore
    private Cliente clienteIndicador;
}