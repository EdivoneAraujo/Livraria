package com.livraria.Controller;

import com.livraria.Model.Vendedor;
import com.livraria.Repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendedores")
public class VendedorController {

    @Autowired
    private VendedorRepository vendedorRepository;

    @GetMapping
    public List<Vendedor> listarTodos() {
        return vendedorRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> cadastrarVendedor(@RequestBody Vendedor novoVendedor) {
        if (novoVendedor.getCpf() != null) {
            novoVendedor.setCpf(novoVendedor.getCpf().replaceAll("\\D", ""));
        }

        if (vendedorRepository.findByCpfOuMatricula(novoVendedor.getCpf()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um funcionário cadastrado com este CPF.");
        }

        if (vendedorRepository.findByCpfOuMatricula(novoVendedor.getMatricula()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um funcionário cadastrado com esta matrícula.");
        }

        Vendedor salvo = vendedorRepository.save(novoVendedor);
        return ResponseEntity.ok(salvo);
    }
}