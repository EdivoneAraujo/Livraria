package com.livraria.Service;

import com.livraria.Model.Autor;
import com.livraria.Model.Livro;
import com.livraria.Repository.AutorRepository;
import com.livraria.Repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Transactional
    public Livro guardar(Livro livro) {
        if (livro.getAutores() == null || livro.getAutores().isEmpty()) {
            throw new IllegalArgumentException("O livro deve possuir pelo menos um autor.");
        }

        Set<Autor> autoresResolvidos = new HashSet<>();
        for (Autor autorRecebido : livro.getAutores()) {
            if (autorRecebido.getIdAutor() != null) {
                Autor autorExistente = autorRepository.findById(autorRecebido.getIdAutor())
                        .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado (ID: " + autorRecebido.getIdAutor() + ")."));
                autoresResolvidos.add(autorExistente);
            } else if (autorRecebido.getNome() != null && !autorRecebido.getNome().isBlank()) {
                String nomeAutor = autorRecebido.getNome().trim();
                Autor autor = autorRepository.findByNomeIgnoreCase(nomeAutor)
                        .orElseGet(() -> autorRepository.save(new Autor(nomeAutor)));
                autoresResolvidos.add(autor);
            } else {
                throw new IllegalArgumentException("Informe o ID ou o nome do autor.");
            }
        }

        livro.setAutores(autoresResolvidos);
        return livroRepository.save(livro);
    }

    public void eliminar(Long id) {
        livroRepository.deleteById(id);
    }
}