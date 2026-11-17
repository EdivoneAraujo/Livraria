package com.livraria.DTO;

import java.util.List;

public class PedidoVendaDTO {
    private String cpfCliente;
    private String nomeCliente;
    private String vendedorCpf; // Recebe o CPF do funcionário que efetuou a venda
    private String formaPagamento;
    private String numeroCartao;
    private List<ItemCarrinhoDTO> itens; // Utiliza o DTO que você já tem no projeto

    // Getters e Setters
    public String getCpfCliente() { return cpfCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getVendedorCpf() { return vendedorCpf; }
    public void setVendedorCpf(String vendedorCpf) { this.vendedorCpf = vendedorCpf; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getNumeroCartao() { return numeroCartao; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }

    public List<ItemCarrinhoDTO> getItens() { return itens; }
    public void setItens(List<ItemCarrinhoDTO> itens) { this.itens = itens; } // Ou o nome correto da lista
}