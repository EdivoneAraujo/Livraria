package com.livraria.Controller;

import com.livraria.DTO.PedidoVendaDTO;
import com.livraria.Model.*;
import com.livraria.Repository.VendaRepository;
import com.livraria.Service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private VendaService vendaService;

    @GetMapping("/vendedor/{matriculaOuCpf}")
    public ResponseEntity<List<Venda>> listarVendasPorVendedor(@PathVariable String matriculaOuCpf) {
        return ResponseEntity.ok(vendaRepository.findByVendedorCpfOuMatricula(matriculaOuCpf));
    }

    @PostMapping("/lote")
    public ResponseEntity<?> registarVendaLote(@RequestBody PedidoVendaDTO pedido) {
        try {
            Venda vendaSalva = vendaService.registarVendaLote(pedido);
            return ResponseEntity.ok(vendaSalva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao processar lote de vendas: " + e.getMessage());
        }
    }

    @GetMapping("/metricas")
    public ResponseEntity<?> obterMetricas() {
        List<Venda> vendas = vendaRepository.findAll();
        double totalFaturado = vendas.stream().mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0.0).sum();
        long totalVendas = vendas.size();

        Map<String, Object> metricas = new HashMap<>();
        metricas.put("totalFaturado", totalFaturado);
        metricas.put("totalVendas", totalVendas);

        String produtoMaisVendido = vendas.stream()
                .filter(v -> v.getItens() != null)
                .flatMap(v -> v.getItens().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                        item -> (item.getLivro() != null && item.getLivro().getTitulo() != null) ? item.getLivro().getTitulo() : "Desconhecido",
                        java.util.stream.Collectors.summingInt(item -> item.getQuantidade() != null ? item.getQuantidade() : 0)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sem dados");
        metricas.put("produtoMaisVendido", produtoMaisVendido);

        String melhorCliente = vendas.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        v -> (v.getCliente() != null && v.getCliente().getNome() != null) ? v.getCliente().getNome() : "Desconhecido",
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sem dados");
        metricas.put("melhorCliente", melhorCliente);

        return ResponseEntity.ok(metricas);
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<Venda>> pesquisarVendas(@RequestParam(required = false) String livro) {
        if (livro == null || livro.trim().isEmpty()) {
            return ResponseEntity.ok(vendaRepository.findAll());
        }
        return ResponseEntity.ok(vendaRepository.pesquisarPorLivroOuCliente(livro));
    }

    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<Venda>> listarVendasPorCliente(@PathVariable String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        return ResponseEntity.ok(vendaRepository.findByClienteDocumento(cpfLimpo));
    }
}