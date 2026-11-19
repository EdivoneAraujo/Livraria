package com.livraria.Controller;

import com.livraria.Model.Cliente;
import com.livraria.Repository.ClienteRepository;
import com.livraria.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable String id) {
        String cpfLimpo = id.replaceAll("\\D", "");
        return clienteService.buscarPorId(cpfLimpo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente novoCliente) {
        if (novoCliente.getCpf() == null || novoCliente.getCpf().isBlank()) {
            return ResponseEntity.badRequest().body("O CPF é obrigatório.");
        }

        novoCliente.setCpf(novoCliente.getCpf().replaceAll("\\D", ""));

        if (clienteRepository.findById(novoCliente.getCpf()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um cliente cadastrado com este CPF/Documento.");
        }

        Cliente salvo = clienteService.guardar(novoCliente);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable String id, @RequestBody Cliente clienteAtualizado) {
        String cpfLimpo = id.replaceAll("\\D", "");
        return clienteService.buscarPorId(cpfLimpo).map(cliente -> {
            // Não alteramos o CPF pois ele é a chave primária (herança JOINED com Pessoa)
            cliente.setNome(clienteAtualizado.getNome());
            cliente.setTelefone(clienteAtualizado.getTelefone());
            cliente.setEmail(clienteAtualizado.getEmail());
            cliente.setSexo(clienteAtualizado.getSexo());
            cliente.setUf(clienteAtualizado.getUf());

            return ResponseEntity.ok(clienteService.guardar(cliente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        String cpfLimpo = id.replaceAll("\\D", "");
        if (clienteService.buscarPorId(cpfLimpo).isPresent()) {
            clienteService.eliminar(cpfLimpo);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}