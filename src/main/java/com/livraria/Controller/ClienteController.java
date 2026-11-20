package com.livraria.Controller;

import com.livraria.DTO.CadastroClienteDTO;
import com.livraria.Model.Cliente;
import com.livraria.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarPorCpf(@PathVariable String cpf) {
        return clienteService.buscarPorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody CadastroClienteDTO dto) {
        try {
            Cliente salvo = clienteService.cadastrarComIndicacao(dto);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para um cliente indicar outro cliente pelo CPF
    @PostMapping("/{idIndicador}/indicar")
    public ResponseEntity<?> indicarCliente(@PathVariable Long idIndicador, @RequestBody Map<String, String> payload) {
        try {
            String cpfIndicado = payload.get("cpfIndicado");
            if (cpfIndicado == null || cpfIndicado.isBlank()) {
                return ResponseEntity.badRequest().body("Informe o CPF do cliente a ser indicado.");
            }
            Cliente indicado = clienteService.registrarIndicacao(idIndicador, cpfIndicado);
            return ResponseEntity.ok(Map.of(
                    "sucesso", true,
                    "mensagem", "Indicação registrada com sucesso para " + indicado.getNome() + "!",
                    "indicado", indicado
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", e.getMessage()
            ));
        }
    }

    // Lista todos os clientes que foram indicados por um cliente específico
    @GetMapping("/{idIndicador}/indicados")
    public ResponseEntity<List<Cliente>> listarIndicados(@PathVariable Long idIndicador) {
        return ResponseEntity.ok(clienteService.listarIndicadosPor(idIndicador));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        return clienteService.buscarPorId(id).map(cliente -> {
            cliente.setNome(clienteAtualizado.getNome());
            cliente.setEmail(clienteAtualizado.getEmail());
            if (clienteAtualizado.getTelefones() != null && !clienteAtualizado.getTelefones().isEmpty()) {
                cliente.setTelefones(clienteAtualizado.getTelefones());
            }
            if (clienteAtualizado.getSenha() != null && !clienteAtualizado.getSenha().isBlank()) {
                cliente.setSenha(clienteAtualizado.getSenha());
            }
            cliente.setSexo(clienteAtualizado.getSexo());
            cliente.setRua(clienteAtualizado.getRua());
            cliente.setNumero(clienteAtualizado.getNumero());
            cliente.setBairro(clienteAtualizado.getBairro());
            cliente.setCidade(clienteAtualizado.getCidade());
            cliente.setUf(clienteAtualizado.getUf());
            cliente.setCep(clienteAtualizado.getCep());

            return ResponseEntity.ok(clienteService.guardar(cliente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (clienteService.buscarPorId(id).isPresent()) {
            clienteService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}