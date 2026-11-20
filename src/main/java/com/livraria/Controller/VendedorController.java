package com.livraria.Controller;

import com.livraria.Model.Vendedor;
import com.livraria.Repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendedores")
@CrossOrigin(origins = "*")
public class VendedorController {

    @Autowired
    private VendedorRepository vendedorRepository;

    @GetMapping
    public List<Vendedor> listarTodos() {
        return vendedorRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> cadastrarVendedor(@RequestBody Vendedor novoVendedor) {
        if (novoVendedor.getCpf() == null || novoVendedor.getCpf().isBlank()) {
            return ResponseEntity.badRequest().body("O CPF do vendedor é obrigatório.");
        }

        novoVendedor.setCpf(novoVendedor.getCpf().replaceAll("\\D", ""));

        if (vendedorRepository.findByCpfOuMatricula(novoVendedor.getCpf()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um funcionário cadastrado com este CPF.");
        }

        if (vendedorRepository.findByCpfOuMatricula(novoVendedor.getMatricula()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um funcionário cadastrado com esta matrícula.");
        }

        if (novoVendedor.getSenha() == null || novoVendedor.getSenha().isBlank()) {
            novoVendedor.setSenha("123456");
        }

        // Vincula o supervisor caso o ID tenha sido enviado no payload
        if (novoVendedor.getSupervisor() != null && novoVendedor.getSupervisor().getIdPessoa() != null) {
            vendedorRepository.findById(novoVendedor.getSupervisor().getIdPessoa())
                    .ifPresent(novoVendedor::setSupervisor);
        }

        Vendedor salvo = vendedorRepository.save(novoVendedor);
        return ResponseEntity.ok(salvo);
    }
}