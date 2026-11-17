package com.livraria.Repository;

import com.livraria.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> { // CORRIGIDO: de Long para String
    Optional<Cliente> findByNome(String nome);

    Optional<Cliente> findByCpf(String cpf);
}