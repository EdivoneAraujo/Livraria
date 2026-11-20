package com.livraria.DTO;

import java.util.List;

public class PedidoVendaDTO {
    private String cpfCliente;
    private String nomeCliente;
    private String ruaCliente;
    private String numeroCliente;
    private String bairroCliente;
    private String cidadeCliente;
    private String ufCliente;
    private String cepCliente;
    private String vendedorIdentificador; // Recebe CPF ou Matrícula do vendedor (opcional)
    private String formaPagamento;
    private String numeroCartao;
    private List<ItemCarrinhoDTO> itens;

    public String getCpfCliente() { return cpfCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getRuaCliente() { return ruaCliente; }
    public void setRuaCliente(String ruaCliente) { this.ruaCliente = ruaCliente; }

    public String getNumeroCliente() { return numeroCliente; }
    public void setNumeroCliente(String numeroCliente) { this.numeroCliente = numeroCliente; }

    public String getBairroCliente() { return bairroCliente; }
    public void setBairroCliente(String bairroCliente) { this.bairroCliente = bairroCliente; }

    public String getCidadeCliente() { return cidadeCliente; }
    public void setCidadeCliente(String cidadeCliente) { this.cidadeCliente = cidadeCliente; }

    public String getUfCliente() { return ufCliente; }
    public void setUfCliente(String ufCliente) { this.ufCliente = ufCliente; }

    public String getCepCliente() { return cepCliente; }
    public void setCepCliente(String cepCliente) { this.cepCliente = cepCliente; }

    public String getVendedorIdentificador() { return vendedorIdentificador; }
    public void setVendedorIdentificador(String vendedorIdentificador) { this.vendedorIdentificador = vendedorIdentificador; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getNumeroCartao() { return numeroCartao; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }

    public List<ItemCarrinhoDTO> getItens() { return itens; }
    public void setItens(List<ItemCarrinhoDTO> itens) { this.itens = itens; }
}