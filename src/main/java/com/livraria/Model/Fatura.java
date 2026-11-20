package com.livraria.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fatura")
public class Fatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fatura")
    private Long idFatura;

    @OneToOne
    @JoinColumn(name = "id_pedido", nullable = false, unique = true)
    private Pedido pedido;

    @Column(name = "numero_fatura", nullable = false, unique = true, length = 50)
    private String numeroFatura;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    @PrePersist
    protected void onCreate() {
        if (dataEmissao == null) {
            dataEmissao = LocalDateTime.now();
        }
        if (numeroFatura == null) {
            numeroFatura = "FAT-" + System.currentTimeMillis();
        }
    }

    public Long getIdFatura() { return idFatura; }
    public void setIdFatura(Long idFatura) { this.idFatura = idFatura; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public String getNumeroFatura() { return numeroFatura; }
    public void setNumeroFatura(String numeroFatura) { this.numeroFatura = numeroFatura; }

    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }
}