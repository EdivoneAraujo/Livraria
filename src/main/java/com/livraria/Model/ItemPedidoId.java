package com.livraria.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ItemPedidoId implements Serializable {

    @Column(name = "id_pedido")
    private Long idPedido;

    @Column(name = "numero_item")
    private Integer numeroItem;

    public ItemPedidoId() {}

    public ItemPedidoId(Long idPedido, Integer numeroItem) {
        this.idPedido = idPedido;
        this.numeroItem = numeroItem;
    }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public Integer getNumeroItem() { return numeroItem; }
    public void setNumeroItem(Integer numeroItem) { this.numeroItem = numeroItem; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemPedidoId that)) return false;
        return Objects.equals(idPedido, that.idPedido) && Objects.equals(numeroItem, that.numeroItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido, numeroItem);
    }
}