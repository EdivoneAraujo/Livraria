package com.livraria.Service;

import com.livraria.Model.Autor;
import com.livraria.Model.Livro;
import com.livraria.Repository.AutorRepository;
import com.livraria.Repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    public Optional<Livro> buscarPorId(Long id) {
        return livroRepository.findById(id);
    }

    public Livro guardar(Livro livro) {
        if (livro.getAutor() == null) {
            throw new IllegalArgumentException("O autor do livro é obrigatório.");
        }

        if (livro.getAutor().getId() != null) {
            Autor autorExistente = autorRepository.findById(livro.getAutor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado (ID: " + livro.getAutor().getId() + ")."));
            livro.setAutor(autorExistente);
        } else if (livro.getAutor().getNome() != null && !livro.getAutor().getNome().isBlank()) {
            String nomeAutor = livro.getAutor().getNome().trim();
            Autor autor = autorRepository.findByNomeIgnoreCase(nomeAutor)
                    .orElseGet(() -> autorRepository.save(new Autor(nomeAutor)));
            livro.setAutor(autor);
        } else {
            throw new IllegalArgumentException("Informe o ID ou o nome do autor.");
        }

        return livroRepository.save(livro);
    }

    public void eliminar(Long id) {
        livroRepository.deleteById(id);
    }
}