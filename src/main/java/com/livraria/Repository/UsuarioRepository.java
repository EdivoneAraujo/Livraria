package com.livraria.Repository;

import com.livraria.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> { // CORRIGIDO: de Long para String

    Optional<Usuario> findByEmail(String email);
}