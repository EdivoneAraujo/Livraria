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
        if (livro.getAutor() != null && livro.getAutor().getId() == null && livro.getAutor().getNome() != null) {
            String nomeAutor = livro.getAutor().getNome().trim();
            Autor autor = autorRepository.findByNomeIgnoreCase(nomeAutor)
                    .orElseGet(() -> autorRepository.save(new Autor(nomeAutor)));
            livro.setAutor(autor);
        }
        return livroRepository.save(livro);
    }

    public void eliminar(Long id) {
        livroRepository.deleteById(id);
    }
}