package com.livraria.Service;

import com.livraria.DTO.PedidoVendaDTO;
import com.livraria.Model.*;
import com.livraria.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VendedorRepository vendedorRepository;
    @Autowired
    private FaturaRepository faturaRepository;

    @Transactional
    public Venda registarVendaLote(PedidoVendaDTO pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio.");
        }

        // 1. Valida estoque de TODOS os itens antes de modificar qualquer dado
        for (var itemDto : pedido.getItens()) {
            Livro livro = livroRepository.findById(itemDto.getLivroId())
                    .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado (ID: " + itemDto.getLivroId() + ")."));
            if (livro.getQuantidadeEstoque() < itemDto.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o livro: " + livro.getTitulo());
            }
        }

        // 2. Padroniza CPF do Cliente
        String cpfLimpo = (pedido.getCpfCliente() != null)
                ? pedido.getCpfCliente().replaceAll("\\D", "")
                : "00000000000";
        if (cpfLimpo.isEmpty()) cpfLimpo = "00000000000";

        final String cpfFinal = cpfLimpo;
        Cliente cliente = clienteRepository.findByCpf(cpfFinal)
                .orElseGet(() -> {
                    Cliente novo = new Cliente();
                    novo.setCpf(cpfFinal);
                    novo.setNome((pedido.getNomeCliente() != null && !pedido.getNomeCliente().isBlank())
                            ? pedido.getNomeCliente().trim() : "Cliente Balcão");
                    novo.setUf("PE");
                    novo.setSexo("Outro");
                    return clienteRepository.save(novo);
                });

        if (pedido.getNomeCliente() != null && !pedido.getNomeCliente().isBlank()) {
            cliente.setNome(pedido.getNomeCliente().trim());
            cliente = clienteRepository.save(cliente);
        }

        Venda novaVenda = new Venda();
        novaVenda.setCliente(cliente);
        novaVenda.setDataVenda(LocalDateTime.now());

        // 3. Associação segura do Vendedor
        if (pedido.getVendedorCpf() != null && !pedido.getVendedorCpf().isBlank()) {
            String docVendedor = pedido.getVendedorCpf().trim();
            Vendedor vendedor = vendedorRepository.findByCpfOuMatricula(docVendedor)
                    .orElseGet(() -> {
                        Vendedor novoVendedor = new Vendedor();
                        novoVendedor.setCpf(docVendedor);
                        novoVendedor.setMatricula(docVendedor);
                        novoVendedor.setNome("Vendedor " + docVendedor);
                        return vendedorRepository.save(novoVendedor);
                    });
            novaVenda.setVendedor(vendedor);
        }

        // 4. Forma de pagamento segura
        FormaPagamento formaPag = FormaPagamento.CARTAO;
        if (pedido.getFormaPagamento() != null && pedido.getFormaPagamento().trim().equalsIgnoreCase("PIX")) {
            formaPag = FormaPagamento.PIX;
        }
        novaVenda.setFormaPagamento(formaPag);

        // 5. Baixa no estoque e montagem dos itens
        List<ItemVenda> listaItensVenda = new ArrayList<>();
        double valorTotalGeral = 0.0;
        StringBuilder alertas = new StringBuilder();

        for (var itemDto : pedido.getItens()) {
            Livro livro = livroRepository.findById(itemDto.getLivroId()).get();
            livro.setQuantidadeEstoque(livro.getQuantidadeEstoque() - itemDto.getQuantidade());
            livroRepository.save(livro);

            if (livro.getQuantidadeEstoque() <= livro.getEstoqueMinimo()) {
                alertas.append("Atenção: O livro '").append(livro.getTitulo()).append("' atingiu o estoque mínimo. ");
            }

            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setVenda(novaVenda);
            itemVenda.setLivro(livro);
            itemVenda.setQuantidade(itemDto.getQuantidade());
            itemVenda.setPrecoUnitario(livro.getPreco());

            listaItensVenda.add(itemVenda);
            valorTotalGeral += (livro.getPreco() * itemDto.getQuantidade());
        }

        novaVenda.setItens(listaItensVenda);
        novaVenda.setTotal(valorTotalGeral);
        if (!alertas.isEmpty()) {
            novaVenda.setAlertaEstoque(alertas.toString().trim());
        }

        Venda vendaSalva = vendaRepository.save(novaVenda);

        // 6. Gera a Fatura vinculada à venda
        Fatura fatura = new Fatura();
        fatura.setVenda(vendaSalva);
        faturaRepository.save(fatura);

        return vendaSalva;
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }
}