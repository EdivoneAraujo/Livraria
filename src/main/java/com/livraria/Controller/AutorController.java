package com.livraria.Controller;

import com.livraria.Model.Autor;
import com.livraria.Repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin(origins = "*")
public class AutorController {

    @Autowired
    private AutorRepository autorRepository;

    @GetMapping
    public List<Autor> listarTodos() {
        return autorRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> cadastrarAutor(@RequestBody Autor novoAutor) {
        if (novoAutor.getNome() == null || novoAutor.getNome().isBlank()) {
            return ResponseEntity.badRequest().body("O nome do autor é obrigatório.");
        }
        Autor salvo = autorRepository.save(novoAutor);
        return ResponseEntity.ok(salvo);
    }
}