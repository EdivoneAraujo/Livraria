package com.livraria.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fatura")
public class Fatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @Column(nullable = false, unique = true)
    private String numeroFatura; // Ex: FAT-2026-0001

    @Column(nullable = false)
    private LocalDateTime dataEmissao;

    @PrePersist
    protected void onCreate() {
        dataEmissao = LocalDateTime.now();
        numeroFatura = "FAT-" + System.currentTimeMillis(); // Gerador simples de número único
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Venda getVenda() { return venda; }
    public void setVenda(Venda venda) { this.venda = venda; }

    public String getNumeroFatura() { return numeroFatura; }
    public void setNumeroFatura(String numeroFatura) { this.numeroFatura = numeroFatura; }

    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }
}