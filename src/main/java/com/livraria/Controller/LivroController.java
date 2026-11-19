package com.livraria.Controller;

import com.livraria.Model.Livro;
import com.livraria.Service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping("/acervo")
    public List<Livro> listarAcervo() {
        return livroService.listarTodos();
    }

    @GetMapping
    public List<Livro> listarTodos() {
        return livroService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarPorId(@PathVariable Long id) {
        return livroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrarLivro(@RequestBody Livro novoLivro) {
        try {
            if (novoLivro.getImagem() == null || novoLivro.getImagem().isEmpty()) {
                novoLivro.setImagem("");
            }
            Livro salvo = livroService.guardar(novoLivro);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Livro livroAtualizado) {
        return livroService.buscarPorId(id).map(livro -> {
            livro.setTitulo(livroAtualizado.getTitulo());
            livro.setTematica(livroAtualizado.getTematica());
            livro.setAutor(livroAtualizado.getAutor());
            livro.setPreco(livroAtualizado.getPreco());
            livro.setQuantidadeEstoque(livroAtualizado.getQuantidadeEstoque());
            livro.setEstoqueMinimo(livroAtualizado.getEstoqueMinimo());

            if (livroAtualizado.getImagem() != null && !livroAtualizado.getImagem().isEmpty()) {
                livro.setImagem(livroAtualizado.getImagem());
            }

            try {
                return ResponseEntity.ok(livroService.guardar(livro));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (livroService.buscarPorId(id).isPresent()) {
            livroService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}