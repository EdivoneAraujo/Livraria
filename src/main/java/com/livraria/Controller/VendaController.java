package com.livraria.Controller;

import com.livraria.DTO.PedidoVendaDTO;
import com.livraria.Model.Pedido;
import com.livraria.Repository.PedidoRepository;
import com.livraria.Service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
public class VendaController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private VendaService vendaService;

    @GetMapping("/vendedor/{matriculaOuCpf}")
    public ResponseEntity<List<Pedido>> listarVendasPorVendedor(@PathVariable String matriculaOuCpf) {
        return ResponseEntity.ok(pedidoRepository.findByVendedorCpfOuMatricula(matriculaOuCpf));
    }

    @PostMapping("/lote")
    public ResponseEntity<?> registrarVendaLote(@RequestBody PedidoVendaDTO pedidoDto) {
        try {
            Pedido pedidoSalvo = vendaService.registrarVendaLote(pedidoDto);
            return ResponseEntity.ok(pedidoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao processar lote de pedidos: " + e.getMessage());
        }
    }

    @GetMapping("/metricas")
    public ResponseEntity<?> obterMetricas() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        double totalFaturado = pedidos.stream()
                .mapToDouble(p -> p.getValorTotal() != null ? p.getValorTotal() : 0.0)
                .sum();
        long totalVendas = pedidos.size();

        Map<String, Object> metricas = new HashMap<>();
        metricas.put("totalFaturado", totalFaturado);
        metricas.put("totalVendas", totalVendas);

        String produtoMaisVendido = pedidos.stream()
                .filter(p -> p.getItens() != null)
                .flatMap(p -> p.getItens().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                        item -> (item.getLivro() != null && item.getLivro().getTitulo() != null) ? item.getLivro().getTitulo() : "Desconhecido",
                        java.util.stream.Collectors.summingInt(item -> item.getQuantidade() != null ? item.getQuantidade() : 0)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sem dados");
        metricas.put("produtoMaisVendido", produtoMaisVendido);

        String melhorCliente = pedidos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> (p.getCliente() != null && p.getCliente().getNome() != null) ? p.getCliente().getNome() : "Desconhecido",
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
    public ResponseEntity<List<Pedido>> pesquisarVendas(@RequestParam(required = false) String livro) {
        if (livro == null || livro.trim().isEmpty()) {
            return ResponseEntity.ok(pedidoRepository.findAll());
        }
        return ResponseEntity.ok(pedidoRepository.pesquisarPorLivroOuCliente(livro));
    }

    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<Pedido>> listarVendasPorCliente(@PathVariable String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        return ResponseEntity.ok(pedidoRepository.findByClienteDocumento(cpfLimpo));
    }
}