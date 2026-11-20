package com.livraria.Service;

import com.livraria.DTO.PedidoVendaDTO;
import com.livraria.Model.*;
import com.livraria.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VendaService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VendedorRepository vendedorRepository;
    @Autowired
    private FaturaRepository faturaRepository;

    @Transactional
    public Pedido registrarVendaLote(PedidoVendaDTO dto) {

        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio.");
        }

        // 1. Valida estoque de TODOS os itens antes de modificar qualquer dado
        for (var itemDto : dto.getItens()) {
            Livro livro = livroRepository.findById(itemDto.getLivroId())
                    .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado (ID: " + itemDto.getLivroId() + ")."));
            if (livro.getQuantidadeEstoque() < itemDto.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o livro: " + livro.getTitulo());
            }
        }

        // 2. Busca ou cria o Cliente respeitando todas as colunas obrigatórias de Pessoa e Cliente
        String cpfLimpo = (dto.getCpfCliente() != null)
                ? dto.getCpfCliente().replaceAll("\\D", "")
                : "00000000000";
        if (cpfLimpo.isEmpty()) cpfLimpo = "00000000000";

        final String cpfFinal = cpfLimpo;
        String ufFinal = (dto.getUfCliente() != null && !dto.getUfCliente().isBlank())
                ? dto.getUfCliente().trim().toUpperCase() : "PE";

        Cliente cliente = clienteRepository.findByCpf(cpfFinal)
                .orElseGet(() -> {
                    Cliente novo = new Cliente();
                    novo.setCpf(cpfFinal);
                    novo.setNome((dto.getNomeCliente() != null && !dto.getNomeCliente().isBlank())
                            ? dto.getNomeCliente().trim() : "Cliente Balcão");
                    novo.setEmail(cpfFinal + "@cliente.livraria.com");
                    novo.setSenha("123456");
                    novo.setSexo("Outro");
                    novo.setRua((dto.getRuaCliente() != null && !dto.getRuaCliente().isBlank())
                            ? dto.getRuaCliente().trim() : "Não informado");
                    novo.setNumero((dto.getNumeroCliente() != null && !dto.getNumeroCliente().isBlank())
                            ? dto.getNumeroCliente().trim() : "S/N");
                    novo.setBairro((dto.getBairroCliente() != null && !dto.getBairroCliente().isBlank())
                            ? dto.getBairroCliente().trim() : "Centro");
                    novo.setCidade((dto.getCidadeCliente() != null && !dto.getCidadeCliente().isBlank())
                            ? dto.getCidadeCliente().trim() : "Belo Jardim");
                    novo.setUf(ufFinal);
                    novo.setCep((dto.getCepCliente() != null && !dto.getCepCliente().isBlank())
                            ? dto.getCepCliente().trim() : "55150-000");
                    return clienteRepository.save(novo);
                });

        boolean clienteAtualizado = false;
        if (dto.getNomeCliente() != null && !dto.getNomeCliente().isBlank()) {
            cliente.setNome(dto.getNomeCliente().trim());
            clienteAtualizado = true;
        }
        if (dto.getUfCliente() != null && !dto.getUfCliente().isBlank()) {
            cliente.setUf(ufFinal);
            clienteAtualizado = true;
        }
        if (dto.getRuaCliente() != null && !dto.getRuaCliente().isBlank()) {
            cliente.setRua(dto.getRuaCliente().trim());
            clienteAtualizado = true;
        }
        if (dto.getNumeroCliente() != null && !dto.getNumeroCliente().isBlank()) {
            cliente.setNumero(dto.getNumeroCliente().trim());
            clienteAtualizado = true;
        }
        if (dto.getBairroCliente() != null && !dto.getBairroCliente().isBlank()) {
            cliente.setBairro(dto.getBairroCliente().trim());
            clienteAtualizado = true;
        }
        if (dto.getCidadeCliente() != null && !dto.getCidadeCliente().isBlank()) {
            cliente.setCidade(dto.getCidadeCliente().trim());
            clienteAtualizado = true;
        }
        if (dto.getCepCliente() != null && !dto.getCepCliente().isBlank()) {
            cliente.setCep(dto.getCepCliente().trim());
            clienteAtualizado = true;
        }
        if (clienteAtualizado) {
            cliente = clienteRepository.save(cliente);
        }

        Pedido novoPedido = new Pedido();
        novoPedido.setCliente(cliente);
        novoPedido.setDataVenda(LocalDateTime.now());

        // 3. Associação opcional do Vendedor (por CPF ou Matrícula)
        if (dto.getVendedorIdentificador() != null && !dto.getVendedorIdentificador().isBlank()) {
            String docVendedor = dto.getVendedorIdentificador().trim();
            String docLimpo = docVendedor.replaceAll("\\D", "");

            Vendedor vendedor = vendedorRepository.findByCpfOuMatricula(docLimpo.isEmpty() ? docVendedor : docLimpo)
                    .orElseGet(() -> {
                        Vendedor novoVendedor = new Vendedor();
                        String cpfNovo = docLimpo.isEmpty() ? "99999999999" : docLimpo;
                        novoVendedor.setCpf(cpfNovo);
                        novoVendedor.setMatricula(docVendedor);
                        novoVendedor.setNome("Vendedor " + docVendedor);
                        novoVendedor.setEmail(cpfNovo + "@vendedor.livraria.com");
                        novoVendedor.setSenha("123456");
                        novoVendedor.setSalario(1500.00);
                        novoVendedor.setDataEntrada(LocalDate.now());
                        return vendedorRepository.save(novoVendedor);
                    });
            novoPedido.setVendedor(vendedor);
        }

        // 4. Forma de pagamento
        FormaPagamento formaPag = FormaPagamento.CARTAO;
        if (dto.getFormaPagamento() != null && dto.getFormaPagamento().trim().equalsIgnoreCase("PIX")) {
            formaPag = FormaPagamento.PIX;
        }
        novoPedido.setFormaPagamento(formaPag);

        // Salva o pedido primeiro para gerar id_pedido (necessário para a chave composta de ItemPedido)
        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);

        // 5. Baixa no estoque e montagem dos itens com numero_item sequencial (1, 2, 3...)
        List<ItemPedido> listaItensPedido = new ArrayList<>();
        int numeroItem = 1;

        for (var itemDto : dto.getItens()) {
            Livro livro = livroRepository.findById(itemDto.getLivroId()).orElseThrow();
            livro.setQuantidadeEstoque(livro.getQuantidadeEstoque() - itemDto.getQuantidade());
            livroRepository.save(livro);

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setId(new ItemPedidoId(pedidoSalvo.getIdPedido(), numeroItem++));
            itemPedido.setPedido(pedidoSalvo);
            itemPedido.setLivro(livro);
            itemPedido.setQuantidade(itemDto.getQuantidade());
            itemPedido.setPrecoUnitario(livro.getPreco());

            listaItensPedido.add(itemPedido);
        }

        pedidoSalvo.setItens(listaItensPedido);
        pedidoSalvo = pedidoRepository.save(pedidoSalvo);

        // 6. Gera a Fatura vinculada ao pedido
        Fatura fatura = new Fatura();
        fatura.setPedido(pedidoSalvo);
        faturaRepository.save(fatura);

        return pedidoSalvo;
    }

    public List<Pedido> listarTodas() {
        return pedidoRepository.findAll();
    }
}